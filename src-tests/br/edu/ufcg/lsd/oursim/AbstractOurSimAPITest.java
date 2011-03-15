package br.edu.ufcg.lsd.oursim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.input.workload.WorkloadAbstract;
import br.edu.ufcg.lsd.oursim.io.output.JobPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.TaskPrintOutput;
import br.edu.ufcg.lsd.oursim.io.output.WorkerPrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.FinishJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.SubmitJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.FinishTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.StartedTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.SubmitTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.workerevents.WorkerAvailableEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.workerevents.WorkerUnavailableEvent;

public abstract class AbstractOurSimAPITest {

	protected OurSim oursim;

	protected JobEventCounter jobEventCounter;

	protected TaskEventCounter taskEventCounter;

	@SuppressWarnings("unchecked")
	protected Set<Class> jobEvents;

	@SuppressWarnings("unchecked")
	protected Set<Class> taskEvents;

	@SuppressWarnings("unchecked")
	protected Set<Class> workerEvents;

	protected final int NUMBER_OF_JOBS_BY_PEER = 10;
	protected final int NUMBER_OF_TASKS_BY_JOB = 1;
	protected final int NUMBER_OF_PEERS = 10;
	protected final int NUMBER_OF_RESOURCES_BY_PEER = 10;
	protected final int RESOURCE_MIPS_RATING = 3000;
	protected final int TOTAL_OF_JOBS = NUMBER_OF_PEERS * NUMBER_OF_JOBS_BY_PEER;

	protected final long JOB_LENGTH = 100;
	protected final long JOB_SUBMISSION_TIME = 0;

	protected List<Job> jobs;
	protected List<Peer> peers;

	protected long nextJobId = 0;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {

		jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		jobEvents = new HashSet<Class>();
		jobEvents.add(SubmitJobEvent.class);
		jobEvents.add(FinishJobEvent.class);

		taskEvents = new HashSet<Class>();
		taskEvents.add(SubmitTaskEvent.class);
		taskEvents.add(StartedTaskEvent.class);
		taskEvents.add(FinishTaskEvent.class);

		workerEvents = new HashSet<Class>();
		workerEvents.add(WorkerAvailableEvent.class);
		workerEvents.add(WorkerUnavailableEvent.class);

		jobs = new ArrayList<Job>(TOTAL_OF_JOBS);
		peers = new ArrayList<Peer>(NUMBER_OF_PEERS);

		for (int i = 0; i < NUMBER_OF_PEERS; i++) {
			peers.add(new Peer("p_" + i, NUMBER_OF_RESOURCES_BY_PEER, RESOURCE_MIPS_RATING, FifoSharingPolicy.getInstance()));
		}
	}

	@After
	public void tearDown() throws Exception {
		EventQueue.getInstance().clear();
		JobEventDispatcher.getInstance().clear();
		TaskEventDispatcher.getInstance().clear();
		WorkerEventDispatcher.getInstance().clear();
		JobEventDispatcher.getInstance().removeListener(jobEventCounter);
		TaskEventDispatcher.getInstance().removeListener(taskEventCounter);
		EventQueue.totalNumberOfEvents = 0;
		nextJobId = 0;

	}

	protected Workload generateDefaultWorkload() {
		return generateWorkload(NUMBER_OF_JOBS_BY_PEER, NUMBER_OF_TASKS_BY_JOB, JOB_SUBMISSION_TIME, JOB_LENGTH);
	}

	protected Workload generateWorkload(final int numberOfJobsByPeer, final int numberOfTasksByJob, final long jobsSubmissionTime, final long jobLength) {

		Workload allWorkloads = new WorkloadAbstract() {
			@Override
			protected void setUp() {
			}
		};

		for (final Peer peer : peers) {

			WorkloadAbstract workloadForPeer = new WorkloadAbstract() {
				@Override
				protected void setUp() {
					for (int i = 0; i < numberOfJobsByPeer; i++) {
						Job job = new Job(nextJobId, jobsSubmissionTime, peer);
						for (int j = 0; j < numberOfTasksByJob; j++) {
							job.addTask("", jobLength);
						}
						this.inputs.add(job);
						nextJobId++;
						jobs.add(job);
					}
				}
			};

			allWorkloads.merge(workloadForPeer);
		}

		return allWorkloads;
	}

	public static final Job addJob(long jobId, long submissionTime, long duration, final Peer peer, Collection<Job>... collectionsOfJob) {
		Job job = new Job(jobId, submissionTime, duration, peer);
		for (Collection<Job> collection : collectionsOfJob) {
			collection.add(job);
		}
		return job;
	}

	public static final void setUpToPrintEvents() {
		JobEventDispatcher.getInstance().addListener(new JobPrintOutput());
		TaskEventDispatcher.getInstance().addListener(new TaskPrintOutput());
		WorkerEventDispatcher.getInstance().addListener(new WorkerPrintOutput());
	}

}