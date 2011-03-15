package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.FinishJobEvent;

/**
 * 
 * Event indicating that a task must be finished.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class FinishTaskEvent extends TaskTimedEvent {

	public static final int PRIORITY = 1;

	/**
	 * Creates an event indicating that a task has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the task has been finished.
	 * @param Task
	 *            the task that has been finished.
	 */
	public FinishTaskEvent(long finishTime, Task Task) {
		super(finishTime, PRIORITY, Task);
	}

	@Override
	protected final void doAction() {
		Task task = (Task) source;
		if (!task.isCancelled()) {
			task.finish(time);
			if (task.getSourceJob().isFinished()) {
				// TODO analisar esse acesso direto à fila de eventos
				EventQueue.getInstance().addEvent(new FinishJobEvent(EventQueue.getInstance().getCurrentTime(), task.getSourceJob()));
			}
			TaskEventDispatcher.getInstance().dispatchTaskFinished(task);
		} else {
			System.out.println("estava tentando finalizar uma task já cancelada: " + task);
		}
	}
}
