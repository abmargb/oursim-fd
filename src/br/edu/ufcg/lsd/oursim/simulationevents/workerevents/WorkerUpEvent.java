package br.edu.ufcg.lsd.oursim.simulationevents.workerevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;

/**
 * 
 * Event indicating that a worker becomes up.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class WorkerUpEvent extends WorkerTimedEvent {

	public static final int PRIORITY = 1;

	/**
	 * Creates an event indicating that a worker has become up.
	 * 
	 * @param time
	 *            the time at which the machine has become up.
	 * @param machineName
	 *            the name of the machine that has become up.
	 */
	public WorkerUpEvent(long time, String machineName) {
		super(time, PRIORITY, machineName);
	}

	@Override
	protected void doAction() {
		WorkerEventDispatcher.getInstance().dispatchWorkerUp(this.source, this.time);
	}

}