package br.edu.ufcg.lsd.oursim.simulationevents.jobevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Job;

/**
 * 
 * Event indicating that a job has been started.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class StartedJobEvent extends JobTimedEvent {

	public static final int PRIORITY = 3;

	/**
	 * Creates an event indicating that a job has been finished.
	 * 
	 * @param job
	 *            the job that has been finished.
	 */
	public StartedJobEvent(Job job) {
		super(job.getStartTime(), PRIORITY, job);
	}

	@Override
	protected void doAction() {
		JobEventDispatcher.getInstance().dispatchJobStarted(this.source);
	}

}
