package br.edu.ufcg.lsd.oursim.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.oursim.AbstractOurSimAPITest;
import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.Grid;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Processor;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.input.workload.WorkloadAbstract;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityImp;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public class OurGridPersistentSchedulerMultiplePeersTest extends AbstractOurSimAPITest {

	protected static final long SIMULATION_TIME = 11;

	private JobSchedulerPolicy jobScheduler;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		super.setUp();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRun_1() {
		assertTrue(true);
		assertEquals(true, true);

		peers = new ArrayList<Peer>();

		peers.add(new Peer("peer_" + 1, 1, Processor.EC2_COMPUTE_UNIT.getSpeed(), FifoSharingPolicy.getInstance()));
		peers.add(new Peer("peer_" + 2, 1, Processor.EC2_COMPUTE_UNIT.getSpeed(), FifoSharingPolicy.getInstance()));

		final int jobRuntime = 10;
		Input<AvailabilityRecord> availability = new DedicatedResourcesAvailabilityCharacterization(peers, JOB_SUBMISSION_TIME, SIMULATION_TIME);

		jobs = new ArrayList<Job>();

		Workload allWorkloads = new WorkloadAbstract() {
			@Override
			protected void setUp() {
			}
		};

		final Peer peer1 = peers.get(0);

		WorkloadAbstract workloadForPeer1 = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				int submissionTime = 0;
				for (int i = 0; i < 2; i++) {
					Job job = new Job(nextJobId, submissionTime, peer1);
					for (int j = submissionTime; j < 1; j++) {
						job.addTask("", jobRuntime);
					}
					this.inputs.add(job);
					nextJobId++;
					jobs.add(job);
				}
			}
		};

		final Peer peer2 = peers.get(1);

		WorkloadAbstract workloadForPeer2 = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				int submissionTime = 0;
				for (int i = 0; i < 1; i++) {
					Job job = new Job(nextJobId, submissionTime, peer2);
					for (int j = submissionTime; j < 1; j++) {
						job.addTask("", jobRuntime);
					}
					this.inputs.add(job);
					nextJobId++;
					jobs.add(job);
				}
			}
		};

		allWorkloads.merge(workloadForPeer1);
		allWorkloads.merge(workloadForPeer2);

		Grid grid = new Grid(peers);

		this.jobScheduler = new OurGridPersistentScheduler(peers);

		oursim = new OurSim(EventQueue.getInstance(), grid, jobScheduler, allWorkloads, availability);
		oursim.setActiveEntity(new ActiveEntityImp());
		oursim.start();

		assertEquals(2, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(2, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(2, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(2, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

	}
}
