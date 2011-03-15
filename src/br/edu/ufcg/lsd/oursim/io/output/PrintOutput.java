package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public final class PrintOutput extends OutputAdapter {

	private static final String MISSING_VALUE = "NA";

	private static final String SEP = ":";

	private static final String SUBMIT_LABEL = "U";

	private static final String START_LABEL = "S";

	private static final String PREEMPT_LABEL = "P";

	private static final String FINISH_LABEL = "F";
	
	private static final String HEADER = "type".concat(SEP)
								.concat("time").concat(SEP)
								.concat("jobId").concat(SEP)
								.concat("submissionTime").concat(SEP)
								.concat("startTime").concat(SEP)
								.concat("expectedDuration").concat(SEP)
								.concat("runtimeDuration").concat(SEP)
								.concat("makeSpan").concat(SEP)
								.concat("size").concat(SEP)
								.concat("tasks").concat(SEP)
								.concat("remoteTasksSize").concat(SEP)
								.concat("remoteTasks").concat(SEP)
								.concat("remoteMakeSpan").concat(SEP)
								.concat("queuingTime").concat(SEP)
								.concat("numberOfPreemption").concat(SEP)
								.concat("localResources").concat(SEP)
								.concat("remoteResources").concat(SEP)
								.concat("remoteRate").concat(SEP)
								.concat("nsl").concat(SEP)
								.concat("cost").concat(SEP)
								.concat("userId").concat(SEP)
								.concat("peerId");;

	/**
	 * the stream where the results will be printed out.
	 */
	private PrintStream out;

	private boolean showProgress;

	/**
	 * An default constructor. Using this constructor the results will be
	 * printed out in the default output.
	 */
	public PrintOutput() {
		this.out = System.out;
	}

	public PrintOutput(File file) throws IOException {
		this(file, false);
	}

	/**
	 * Using this constructor the results will be printed out in the file called
	 * <code>fileName</code>.
	 * 
	 * @param fileName
	 *            The name of the file where the results will be printed out.
	 * @throws FileNotFoundException
	 */
	public PrintOutput(File file, boolean showProgress) throws IOException {
		this.showProgress = showProgress;
		this.out = new PrintStream(file);
		this.out.println(HEADER);
	}

	@Override
	public final void jobSubmitted(Event<Job> jobEvent) {
		if (showProgress) {
			Job job = jobEvent.getSource();

			long jobId = job.getId();
			long submissionTime = job.getSubmissionTime();
			long duration = job.getDuration();

			StringBuilder sb = new StringBuilder(SUBMIT_LABEL);
			sb.append(SEP)
			.append(submissionTime).append(SEP)
			.append(jobId).append(SEP)
			.append(submissionTime).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(duration).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(job.getUserId());
			this.out.println(sb);
		}
	}

	@Override
	public final void jobStarted(Event<Job> jobEvent) {
		if (showProgress) {
			Job job = jobEvent.getSource();
			
			long jobId = job.getId();
			long submissionTime = job.getSubmissionTime();
			long finishTime = job.getFinishTime();
			long startTime = job.getStartTime();
			long duration = job.getDuration();
			long runTimeDuration = job.getRunningTime();
			long makeSpan = job.getMakeSpan();
			double cost = job.getCost();
			long queuingTime = job.getQueueingTime();
			long numberOfPreemptions = job.getNumberOfPreemptions();

			StringBuilder sb = new StringBuilder(START_LABEL);
			sb.append(SEP)
			.append(finishTime).append(SEP)
			.append(jobId).append(SEP)
			.append(submissionTime).append(SEP)
			.append(startTime).append(SEP)
			.append(duration).append(SEP)
			.append(runTimeDuration).append(SEP)
			.append(makeSpan).append(SEP)
			.append(cost).append(SEP)
			.append(queuingTime).append(SEP)
			.append(numberOfPreemptions).append(SEP)
			.append(job.getUserId());
			this.out.println(sb);
		}
	}

	@Override
	public final void jobPreempted(Event<Job> jobEvent) {
		if (showProgress) {
			Job job = jobEvent.getSource();
			
			long jobId = job.getId();
			long submissionTime = job.getSubmissionTime();
			double cost = job.getCost();
			long duration = job.getDuration();
			long numberOfPreemptions = job.getNumberOfPreemptions();

			StringBuilder sb = new StringBuilder(PREEMPT_LABEL);
			sb.append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(jobId).append(SEP)
			.append(submissionTime).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(duration).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(cost).append(SEP)
			.append(MISSING_VALUE).append(SEP)
			.append(numberOfPreemptions).append(SEP)
			.append(job.getUserId());
			this.out.println(sb);
		}
	}

	@Override
	public final void jobFinished(Event<Job> jobEvent) {

		Job job = jobEvent.getSource();
		
		long jobId = job.getId();
		long submissionTime = job.getSubmissionTime();
		long finishTime = job.getFinishTime();
		long startTime = job.getStartTime();
		long runTimeDuration = job.getRunningTime();
		long duration = job.getDuration();
		long makeSpan = job.getMakeSpan();
		long remoteMakeSpan = 0;
//		double cost = job.getCost();
		StringBuilder tasks = new StringBuilder();
		StringBuilder remoteTasks = new StringBuilder();
		String sep = "";
		String sep2 = "";
		int remTasksSize = 0;

		Long remoteTasksRuntimeSum = 0l;
		for (Task Task : job.getTasks()) {
			tasks.append(sep).append(Task.getDuration());
			if (!Task.hasLocallyRunned()) {
				remoteTasks.append(sep2).append(Task.getDuration());
				remoteTasksRuntimeSum += Task.getDuration();
				remTasksSize++;
				sep2 = ";";
				remoteMakeSpan = Math.max(remoteMakeSpan, Task.getMakeSpan());
				if (Task.getMakeSpan()>job.getMakeSpan()) {
					System.out.println(Task);
				}
			}
			sep = ";";
		}
	
//		tasks.append("]");
		String size = job.getTasks().size()+"";
		String tasksStr = tasks.toString();
		String remoteTasksSize = remTasksSize +"";
		long queuingTime = job.getQueueingTime();
		long numberOfPreemptions = job.getNumberOfPreemptions();

		StringBuilder sb = new StringBuilder(FINISH_LABEL);
		String remoteTasksTMP = remoteTasks.toString().isEmpty() ? MISSING_VALUE : remoteTasks.toString();
		sb.append(SEP)
		.append(finishTime).append(SEP)
		.append(jobId).append(SEP)
		.append(submissionTime).append(SEP)
		.append(startTime).append(SEP)
		.append(duration).append(SEP)
		.append(runTimeDuration).append(SEP)
		.append(makeSpan).append(SEP)
		.append(size).append(SEP)
		.append(tasksStr).append(SEP)
		.append(remoteTasksSize).append(SEP)
		.append(remoteTasksTMP).append(SEP)
		.append(remoteMakeSpan+"").append(SEP)
		.append(queuingTime).append(SEP)
		.append(numberOfPreemptions).append(SEP)
		.append(job.numberOfLocalResourcesUsed()).append(SEP)
		.append(job.numberOfRemoteResourcesUsed()).append(SEP)
		.append(job.numberOfRemoteResourcesUsed()/(job.getTasks().size()*1.0)).append(SEP)
		.append(job.getNSL()).append(SEP)
		.append(job.getCost()).append(SEP)
		.append(job.getUserId()).append(SEP)
		.append(job.getSourcePeer().getName());
		this.out.println(sb);
	}

	@Override
	public final void close() {
		this.out.close();
	}

}