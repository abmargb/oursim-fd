package br.edu.ufcg.lsd.oursim.simulationevents.jobevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Job;

/**
 * 
 * Event indicating that a job was preempted. A job is considered preempted when
 * all its tasks have been preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public class PreemptedJobEvent extends JobTimedEvent {

	public static final int PRIORITY = 2;

	public PreemptedJobEvent(long time, Job job) {
		super(time, PRIORITY, job);
	}

	@Override
	protected void doAction() {
		JobEventDispatcher.getInstance().dispatchJobPreempted(this.source, this.time);
	}

}
