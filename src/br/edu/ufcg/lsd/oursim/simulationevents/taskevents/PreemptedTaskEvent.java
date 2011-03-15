package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.PreemptedJobEvent;

/**
 * 
 * Event indicating that a task was preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class PreemptedTaskEvent extends TaskTimedEvent {

	// public static final int PRIORITY = 2;
	// public static final int PRIORITY = 4;
	public static final int PRIORITY = -3;

	/**
	 * Creates an event indicating that a task has been preempted.
	 * 
	 * @param preemptionTime
	 *            the time at which the task has been preempted.
	 * @param Task
	 *            the task that has been preempted.
	 */
	public PreemptedTaskEvent(long preemptionTime, Task Task) {
		super(preemptionTime, PRIORITY, Task);
	}

	@Override
	protected void doAction() {
		Task Task = this.source;
		if (!Task.isCancelled()) {
			// TODO XXX definir onde essas ações devem ficar: se em quem a
			// executa ou se no evento disparado.
			// task.preempt(this.time);
			TaskEventDispatcher.getInstance().dispatchTaskPreempted(Task, this.time);
			// TODO: se for um job de uma task só, avisa que o job foi
			// preemptado
			if (Task.getSourceJob().isSingleJob() && Task.isAllReplicasFailed() && Task.getSourceJob().getLastPreemptionTime() < this.time) {
				// EventQueue.getInstance().addPreemptedJobEvent(EventQueue.getInstance().getCurrentTime(),
				// task.getSourceJob());
				Task.getSourceJob().preempt(time);
				EventQueue.getInstance().addEvent(new PreemptedJobEvent(EventQueue.getInstance().getCurrentTime(), Task.getSourceJob()));
			}
		} else {
			System.out.println("Tentando preemptar uma task que já foi cancelada: " + Task);
		}
	}

}
