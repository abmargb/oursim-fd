package br.edu.ufcg.lsd.oursim.io.output;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventListener;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class ComputingElementEventCounter implements JobEventListener, TaskEventListener {

	private JobEventCounter jobEventCounter;
	private TaskEventCounter taskEventCounter;

	public ComputingElementEventCounter() {
		this.jobEventCounter = new JobEventCounter();
		this.taskEventCounter = new TaskEventCounter();
	}

	public long getSumOfTasksMakespan() {
		return this.taskEventCounter.getSumOfTasksMakespan();
	}
	
	public long getSumOfJobsMakespan() {
		return this.jobEventCounter.getSumOfJobsMakespan();
	}

	public final int getNumberOfSubmittedTasks() {
		return this.taskEventCounter.getNumberOfSubmittedTasks();
	}

	public final int getNumberOfFinishedTasks() {
		return this.taskEventCounter.getNumberOfFinishedTasks();
	}

	public final int getNumberOfPreemptionsForAllTasks() {
		return this.taskEventCounter.getNumberOfPreemptionsForAllTasks();
	}

	public final int getNumberOfPreemptionsForAllJobs() {
		return this.jobEventCounter.getNumberOfPreemptionsForAllJobs();
	}

	public double getTotalCostOfAllFinishedJobs() {
		return this.jobEventCounter.getTotalCostOfAllFinishedJobs();
	}

	public double getTotalCostOfAllPreemptedJobs() {
		return this.jobEventCounter.getTotalCostOfAllPreemptedJobs();
	}

	public final int getNumberOfSubmittedJobs() {
		return this.jobEventCounter.getNumberOfSubmittedJobs();
	}

	public final int getNumberOfFinishedJobs() {
		return this.jobEventCounter.getNumberOfFinishedJobs();
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
		this.jobEventCounter.jobFinished(jobEvent);
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
		this.jobEventCounter.jobPreempted(jobEvent);
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
		this.jobEventCounter.jobStarted(jobEvent);
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
		this.jobEventCounter.jobSubmitted(jobEvent);
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
		this.taskEventCounter.taskCancelled(taskEvent);
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		this.taskEventCounter.taskFinished(taskEvent);
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		this.taskEventCounter.taskPreempted(taskEvent);
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
		this.taskEventCounter.taskStarted(taskEvent);
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		this.taskEventCounter.taskSubmitted(taskEvent);
	}

	@Override
	public int compareTo(EventListener o) {
		return this.hashCode() - o.hashCode();
	}

}