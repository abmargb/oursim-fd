package br.edu.ufcg.lsd.oursim.io.input.workload;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventListener;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;

public class DynamicWorkload extends InputAbstract<Job> implements JobEventListener {

	Job nextJob;

	@Override
	public Job peek() {
		return this.nextJob;
	}

	@Override
	public Job poll() {
		Job polledJob = this.peek();
		this.nextJob = null;
		return polledJob;
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
		Job theJob = jobEvent.getSource();
		Job newJob = null; // list.next(theJob)
		// newJob.setSubmitTime(jobEvent.getTime() + theJob.GetThinkTime());
		this.inputs.add(newJob);
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
	}

	@Override
	public int compareTo(EventListener o) {
		return this.hashCode() - o.hashCode();
	}

}
