package br.edu.ufcg.lsd.oursim.simulationevents.workerevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * 
 * Event indicating that a worker becomes unavailable.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerUnavailableEvent extends WorkerTimedEvent {

	public static final int PRIORITY = -2;
	
	/**
	 * Creates an event indicating that a worker has become unavailable.
	 * 
	 * @param time
	 *            the time at which the machine has become unavailable.
	 * @param machineName
	 *            the name of the machine that has become unavailable.
	 */
	public WorkerUnavailableEvent(long time, String machineName) {
		super(time, PRIORITY, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerUnavailable(this.source, this.time);
	}

}