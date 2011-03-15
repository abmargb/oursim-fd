package br.edu.ufcg.lsd.oursim.simulationevents.jobevents;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.simulationevents.TimedEventAbstract;

/**
 * 
 * The root class to all job's related events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public abstract class JobTimedEvent extends TimedEventAbstract<Job> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 * @param job
	 *            the job this event relates to.
	 */
	public JobTimedEvent(long time, int priority, Job job) {
		super(time, priority, job);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String type = this.getType();
		String time = Long.toString(this.getTime());
		String peer = this.source.getSourcePeer().getName();
		String taskId = null;
		String jobId = Long.toString(this.source.getId());
		String makespan = this.source.getMakeSpan() + "";
		String runningTime = this.source.getRunningTime() + "";
		String queuingTime = this.source.getQueueingTime() + "";
		sb.append(type).append(" ").append(time).append(" ").append(taskId).append(" ").append(jobId).append(" ").append(peer).append(" ").append(makespan)
				.append(" ").append(runningTime).append(" ").append(queuingTime);
		return sb.toString();

	}

}
