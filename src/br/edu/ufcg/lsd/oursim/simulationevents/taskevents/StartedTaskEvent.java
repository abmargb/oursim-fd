package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * Event indicating that a task has been started.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class StartedTaskEvent extends TaskTimedEvent {

//	public static final int PRIORITY = 4;
//	public static final int PRIORITY = 2;
	public static final int PRIORITY = -4;

	/**
	 * Creates an event indicating that a task has been started.
	 * 
	 * @param Task
	 *            the task that has been started.
	 */
	public StartedTaskEvent(Task Task) {
		super(Task.getStartTime(), PRIORITY, Task);
	}

	@Override
	protected void doAction() {
		Task Task = (Task) source;
		if (!Task.isCancelled()) {
			TaskEventDispatcher.getInstance().dispatchTaskStarted(Task);
		} else{
			System.out.println("tava tentando startar uma task já cancelada: " + Task);
		}
	}

}
