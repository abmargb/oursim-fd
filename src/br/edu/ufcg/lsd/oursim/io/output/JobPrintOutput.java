package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.IOException;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class JobPrintOutput extends OutputAdapter {

	public JobPrintOutput() {
		super();
	}

	public JobPrintOutput(File file) throws IOException {
		super(file);
		super.appendln("type:time:jobId");
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();

		long id = job.getId();
		long submissionTime = job.getSubmissionTime();

		super.appendln("U:" + submissionTime + ":" + id);
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();

		long id = job.getId();
		long startTime = job.getStartTime();

		super.appendln("S:" + startTime + ":" + id);
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();

		long id = job.getId();
		long preemptionTime = jobEvent.getTime();

		super.appendln("P:" + preemptionTime + ":" + id);
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();

		long id = job.getId();
		long finishTime = job.getFinishTime();

		super.appendln("F:" + finishTime + ":" + id);
	}

}