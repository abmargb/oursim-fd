package br.edu.ufcg.lsd.oursim.simulationevents.jobevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Job;

/**
 * 
 * Event indicating that a job must be finished.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class FinishJobEvent extends JobTimedEvent {

	public static final int PRIORITY = 1;

	/**
	 * Creates an event indicating that a job has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the job has been finished.
	 * @param job
	 *            the job that has been finished.
	 */
	public FinishJobEvent(long finishTime, Job job) {
		super(finishTime, PRIORITY, job);
	}

	@Override
	protected final void doAction() {
		Job job = (Job) this.source;
		job.finish(this.time);
		JobEventDispatcher.getInstance().dispatchJobFinished(job);
	}

}
