package br.edu.ufcg.lsd.oursim.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventCounter;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.workload.SyntheticWorkload;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.output.PrintOutput;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.NoFSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.OurGridScheduler;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public final class OurSimMainSpikeSolution {

	public static final boolean LOG = false;

	public static final Random RANDOM = new Random(9354269l);

	static final String AVAILABILITY_CHARACTERIZATION_FILE_PATH = "trace_mutka_100-machines_10-hours.txt";

	static final String WORKLOAD_FILE_PATH = "/local/edigley/traces/trace_filtrado1.txt";

	static final boolean USE_NOF = false;

	static final boolean DEDICATED_RESOURCES = true;

	static final int NUMBER_OF_REPLIES = 3;

	private static final String ARGS_STRING =
	// execTime execTimeVar subInterval #Jobs #TasksByJob #Peers #nodesByPeer
	// nodeMIPSRating
	"       100         50            5    10          50     10 		  100           3000";

	private static final String[] ARGS = ARGS_STRING.trim().split("\\s+");

	private static int argsIndex = 0;

	// tempo base de execução do job
	static final int EXEC_TIME = Integer.parseInt(ARGS[argsIndex++]);

	// variância máxima do tempo base de execução (sempre positiva)
	static final int EXEC_TIME_VAR = Integer.parseInt(ARGS[argsIndex++]);

	// intervalo de submissão entre jobs subsequentes
	static final int SUBMISSION_INTERVAL = Integer.parseInt(ARGS[argsIndex++]);

	// quantidade total de jobs do workload
	static final int NUM_JOBS = Integer.parseInt(ARGS[argsIndex++]);

	static final int NUM_TASKS_BY_JOB = Integer.parseInt(ARGS[argsIndex++]);

	static final int NUM_PEERS = Integer.parseInt(ARGS[argsIndex++]);

	// número de nodos do peer
	static final int NUM_RESOURCES_BY_PEER = Integer.parseInt(ARGS[argsIndex++]);

	static final int NODE_MIPS_RATING = Integer.parseInt(ARGS[argsIndex++]);

	private OurSimMainSpikeSolution() {
	}

	private static Map<String, Peer> prepareGrid(String workloadFilePath) throws FileNotFoundException {

		ResourceSharingPolicy sharingPolicy = USE_NOF ? NoFSharingPolicy.getInstance() : FifoSharingPolicy.getInstance();

		Scanner sc = new Scanner(new File(workloadFilePath));

		Map<String, Peer> peers = new HashMap<String, Peer>();
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			for (int i = 0; i < 16; i++) {
				scLine.next();
			}
			String OrigSiteID = scLine.next();

			if (!peers.containsKey(OrigSiteID)) {
				Peer peer = new Peer(OrigSiteID, NUM_RESOURCES_BY_PEER, sharingPolicy);
				peers.put(peer.getName(), peer);
			}
		}

		return peers;

	}

	private static List<Peer> prepareGrid(int numPeers, int numNodesByPeer, int nodeMIPSRating, boolean useNoF) {

		ArrayList<Peer> peers = new ArrayList<Peer>(numPeers);

		ResourceSharingPolicy sharingPolicy = useNoF ? NoFSharingPolicy.getInstance() : FifoSharingPolicy.getInstance();

		for (int i = 0; i < numPeers; i++) {
			peers.add(new Peer("P" + i, numNodesByPeer, nodeMIPSRating, sharingPolicy));
		}

		return peers;

	}

	/**
	 * @param peers
	 *            Vai ser usado para atribuir a origem dos jobs
	 * @return
	 */
	private static Workload prepareSyntheticWorkload(List<Peer> peers) {

		int execTime = OurSimMainSpikeSolution.EXEC_TIME;
		int execTimeVariance = OurSimMainSpikeSolution.EXEC_TIME_VAR;
		int submissionInterval = OurSimMainSpikeSolution.SUBMISSION_INTERVAL;
		int numJobs = OurSimMainSpikeSolution.NUM_JOBS;
		int numTasksByJobs = OurSimMainSpikeSolution.NUM_TASKS_BY_JOB;

		SyntheticWorkload workload = new SyntheticWorkload(execTime, execTimeVariance, submissionInterval, numJobs, numTasksByJobs, peers);

		return workload;

	}

	public static void main(String[] args) throws IOException {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		JobEventDispatcher.getInstance().addListener(new PrintOutput(new File("oursim_trace.txt")));
		JobEventDispatcher.getInstance().addListener(new PrintOutput());

		JobEventCounter jobEventCounter = new JobEventCounter();
		JobEventDispatcher.getInstance().addListener(jobEventCounter);

		TaskEventCounter taskEventCounter = new TaskEventCounter();
		TaskEventDispatcher.getInstance().addListener(taskEventCounter);

		// Map<String, Peer> peersMap = prepareGrid(WORKLOAD_FILE_PATH);
		// List<Peer> peers = new ArrayList<Peer>(peersMap.values());

		List<Peer> peers = prepareGrid(OurSimMainSpikeSolution.NUM_PEERS, OurSimMainSpikeSolution.NUM_RESOURCES_BY_PEER, OurSimMainSpikeSolution.NODE_MIPS_RATING, OurSimMainSpikeSolution.USE_NOF);

		// Workload workload = new GWANorduGridWorkload(WORKLOAD_FILE_PATH,
		// peersMap);

		Workload workload = prepareSyntheticWorkload(peers);

		Input<AvailabilityRecord> availability = OurSimMainSpikeSolution.DEDICATED_RESOURCES ? new DedicatedResourcesAvailabilityCharacterization(peers)
				: new AvailabilityCharacterization(OurSimMainSpikeSolution.AVAILABILITY_CHARACTERIZATION_FILE_PATH);

		System.out.println("Starting Simulation...");

		JobSchedulerPolicy jobScheduler = new OurGridScheduler(peers);

//		new OurSim(EventQueue.getInstance(), peers, jobScheduler, workload, availability).start();

		System.out.println("# Total of  finished  jobs: " + jobEventCounter.getNumberOfFinishedJobs());
		System.out.println("# Total of preempted  jobs: " + jobEventCounter.getNumberOfPreemptionsForAllJobs());
		System.out.println("# Total of  finished tasks: " + taskEventCounter.getNumberOfFinishedTasks());
		System.out.println("# Total of preempted tasks: " + taskEventCounter.getNumberOfPreemptionsForAllTasks());
		System.out.println("# Total of          events: " + EventQueue.totalNumberOfEvents);

		stopWatch.stop();
		System.out.println(stopWatch);

	}
}
