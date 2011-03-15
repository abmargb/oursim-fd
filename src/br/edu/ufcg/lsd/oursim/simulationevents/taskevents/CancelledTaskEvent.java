package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * Event indicating that a task was preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class CancelledTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 0;

	/**
	 * Creates an event indicating that a task has been cancelled.
	 * 
	 * @param cancellingTime
	 *            the time at which the task has been cancelled.
	 * @param Task
	 *            the task that has been preempted.
	 */
	public CancelledTaskEvent(long cancellingTime, Task Task) {
		super(cancellingTime, PRIORITY, Task);
	}

	@Override
	protected void doAction() {
		Task Task = this.source;
		// TODO XXX definir onde essas ações devem ficar: se em quem a executa
		// ou se no evento disparado.
		// task.cancel();
		TaskEventDispatcher.getInstance().dispatchTaskCancelled(Task, this.time);
	}

}
