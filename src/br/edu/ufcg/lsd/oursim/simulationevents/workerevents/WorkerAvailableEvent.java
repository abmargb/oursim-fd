package br.edu.ufcg.lsd.oursim.simulationevents.workerevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * Event indicating that a worker becomes available.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerAvailableEvent extends WorkerTimedEvent {

	public static final int PRIORITY = -1;
	
	/**
	 * Creates an event indicating that a worker has become available.
	 * 
	 * @param time
	 *            the time at which the machine has become available.
	 * @param machineName
	 *            the name of the machine that has become available.
	 */
	public WorkerAvailableEvent(long time, String machineName) {
		super(time, PRIORITY, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerAvailable(this.source, this.time);
	}

}
