package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * Event indicating that a task was submitted. Used when a task has been
 * preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 30/04/2010
 * 
 */
public class SubmitTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 3;

	/**
	 * Creates an event indicating that a task was submitted.
	 * 
	 * @param submitTime
	 *            the time at which the job has been submitted.
	 * @param Task
	 *            the task that has been submitted.
	 */
	public SubmitTaskEvent(long submitTime, Task Task) {
		super(submitTime, PRIORITY, Task);
	}

	@Override
	protected final void doAction() {
		Task Task = (Task) source;
		if (!Task.isCancelled()) {
			TaskEventDispatcher.getInstance().dispatchTaskSubmitted(Task);
		}
	}

}
