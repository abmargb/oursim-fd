package br.edu.ufcg.lsd.oursim.simulationevents.workerevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * 
 * Event indicating that a worker starts to running a task.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerRunningEvent extends WorkerTimedEvent {

	public static final int PRIORITY = 1;
	
	/**
	 * Creates an event indicating that a worker starts to running a task.
	 * 
	 * @param time
	 *            the time at which the machine starts to running a task.
	 * @param machineName
	 *            the name of the machine that starts to running a task.
	 */
	public WorkerRunningEvent(long time, String machineName) {
		super(time, PRIORITY, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerRunning(this.source, this.time);
	}

}