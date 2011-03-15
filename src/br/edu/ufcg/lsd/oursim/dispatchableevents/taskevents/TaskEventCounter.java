package br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Task;

public class TaskEventCounter extends TaskEventListenerAdapter {

	private int numberOfSubmittedTasks = 0;

	private int numberOfFinishedTasks = 0;

	private int numberOfPreemptionsForAllTasks = 0;

	private Set<String> idsOfFinishedTasks = new HashSet<String>();

	private Set<String> idsOfSubmittedTasks = new HashSet<String>();

	private long makespan = 0;

	public long getSumOfTasksMakespan() {
		return makespan;
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		this.numberOfSubmittedTasks++;
		// XXX o id não é único para todas as tasks. A chave única deve
		// considerar task e ID.
		Task Task = taskEvent.getSource();
		idsOfSubmittedTasks.add(Task.getSourceJob().getId() + "-" + Task.getId());

	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		this.numberOfFinishedTasks++;
		// XXX o id não é único para todas as tasks. A chave única deve
		// considerar task e ID.
		Task Task = taskEvent.getSource();
		this.makespan += Task.getMakeSpan();
		idsOfFinishedTasks.add(Task.getSourceJob().getId() + "-" + Task.getId());
	}

	@Override
	public final void taskPreempted(Event<Task> taskEvent) {
		this.numberOfPreemptionsForAllTasks++;
	}

	public final int getNumberOfSubmittedTasks() {
		// return numberOfFinishedTasks;
		return this.idsOfSubmittedTasks.size();
	}

	public final int getNumberOfFinishedTasks() {
		// return numberOfFinishedTasks;
		return this.idsOfFinishedTasks.size();
	}

	public final int getNumberOfPreemptionsForAllTasks() {
		return numberOfPreemptionsForAllTasks;
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
	}

}