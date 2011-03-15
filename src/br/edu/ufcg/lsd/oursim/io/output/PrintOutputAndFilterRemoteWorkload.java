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
public final class PrintOutputAndFilterRemoteWorkload extends OutputAdapter {

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
								.concat("userId");

	/**
	 * the stream where the results will be printed out.
	 */
	private PrintStream out;

	private PrintStream out2;

	private boolean showProgress;

	/**
	 * An default constructor. Using this constructor the results will be
	 * printed out in the default output.
	 */
	public PrintOutputAndFilterRemoteWorkload() {
		this.out = System.out;
	}

	public PrintOutputAndFilterRemoteWorkload(File file) throws IOException {
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
	public PrintOutputAndFilterRemoteWorkload(File file, boolean showProgress) throws IOException {
		this.showProgress = showProgress;
		this.out = new PrintStream(file);
		this.out2 = new PrintStream(new File(file.getParentFile(),file.getName()+"_spot_workload.txt"));
		this.out2.println("submissionTime jobId numberOfTasks avgRuntime tasks userId peerId");
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
//		StringBuilder tasks = new StringBuilder("[");
		StringBuilder tasks = new StringBuilder();
		StringBuilder remoteTasks = new StringBuilder();
		String sep = "";
		String sep2 = "";
		int remTasksSize = 0;
		StringBuilder sb2 = new StringBuilder();

		Long remoteTasksRuntimeSum = 0l;
		for (Task Task : job.getTasks()) {
			tasks.append(sep).append(Task.getDuration());
			if (!Task.hasLocallyRunned()) {
				remoteTasks.append(sep2).append(Task.getDuration());
				remoteTasksRuntimeSum += Task.getDuration();
				remTasksSize++;
				sep2 = ";";
				remoteMakeSpan = Math.max(remoteMakeSpan, Task.getMakeSpan());
			}
			sep = ";";
		}
	
		if (remTasksSize >0) {
			//submissionTime, jobId, numberOfTasks, avgRuntime, tasks, userId, peerId
			sb2.append(submissionTime).append(" ")
			.append(jobId).append(" ")
			.append(remTasksSize).append(" ")
			.append(Math.round(remoteTasksRuntimeSum/(1.0*remTasksSize))).append(" ")
			.append(remoteTasks).append(" ")
			.append(job.getUserId()).append(" ")
			.append(job.getSourcePeer().getName()).append(" ");
			this.out2.println(sb2);
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
		.append(job.getUserId());
		this.out.println(sb);
	}

	@Override
	public final void close() {
		this.out.close();
	}

}