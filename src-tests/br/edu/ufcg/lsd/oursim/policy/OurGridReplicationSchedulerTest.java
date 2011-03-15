package br.edu.ufcg.lsd.oursim.policy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.AbstractOurSimAPITest;
import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.Grid;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.input.workload.WorkloadAbstract;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityImp;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public class OurGridReplicationSchedulerTest extends AbstractOurSimAPITest {

	private static final int REPLICATION_LEVEL = 3;

	/**
	 * Cenário de Teste: Recursos voláteis ao longo da simulação e escalonador
	 * que replica tasks.
	 * 
	 * Asserção: Espera-se que a simulação se dê de forma satisfatório mesmo na
	 * presença de recursos voláteis.
	 * 
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testRun_3() throws Exception {

		final int numberOfPeers = 1;
		final int numberOfResources = 6;

		peers = new ArrayList<Peer>(numberOfPeers);

		final Peer peer = new Peer("the_peer", numberOfResources, RESOURCE_MIPS_RATING, FifoSharingPolicy.getInstance());
		peers.add(peer);

		Input<AvailabilityRecord> availability = new InputAbstract<AvailabilityRecord>() {
			@Override
			protected void setUp() {
				int currentMachineIndex = 0;
				Machine currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 15, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 2));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 12));

				currentMachine = peer.getMachines().get(currentMachineIndex++);

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 2));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 15, 10));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 12));

			}
		};

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
			}

		};

		JobSchedulerPolicy jobScheduler = new OurGridReplicationScheduler(peers, REPLICATION_LEVEL);

		oursim = new OurSim(EventQueue.getInstance(), new Grid(peers), jobScheduler, workload, availability);
		oursim.setActiveEntity(new ActiveEntityImp());
		oursim.start();

		// um dos jobs não vai ser completado por indisponiblidade de máquina.
		int numberOfFinishedJobs = 4;
		int numberOfFinishedTasks = 4;
		assertEquals(numberOfFinishedJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(1, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numberOfFinishedTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		// TODO tem que considerar que há tantos cancelamentos quanto preempcoes
		// assertEquals(11,
		// this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

	}

}