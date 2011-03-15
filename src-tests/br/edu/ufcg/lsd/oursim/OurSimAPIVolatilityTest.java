package br.edu.ufcg.lsd.oursim;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.entities.Grid;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.input.workload.WorkloadAbstract;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.policy.OurGridPersistentScheduler;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityImp;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public class OurSimAPIVolatilityTest extends AbstractOurSimAPITest {

	/**
	 * Cenário de Teste: Recursos voláteis ao longo da simulação.
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
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 25));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 65, 5));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 73, 4));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 6));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 60, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 20));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 40, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 70, 10));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 30, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 60, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 75, 10));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 15, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 40, 20));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 20));

				currentMachine = peer.getMachines().get(currentMachineIndex++);
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 0, 10));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 20, 50));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 74, 2));
				this.inputs.add(new AvailabilityRecord(currentMachine.getName(), 80, 20));

			}
		};

		jobs = new ArrayList<Job>(TOTAL_OF_JOBS);

		Workload workload = new WorkloadAbstract() {
			@Override
			protected void setUp() {
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);
				addJob(nextJobId++, 0, 7, peer, this.inputs, jobs);

				// aos 32 segundos (devido à volatidade) todos os jobs já terão
				// acabado. daí começa a
				// segunda rodada de submissões

				addJob(nextJobId++, 32, 1, peer, this.inputs, jobs);// 5
				addJob(nextJobId++, 32, 2, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);// 8
				addJob(nextJobId++, 32, 5, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 20, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 10, peer, this.inputs, jobs);// 11
				addJob(nextJobId++, 32, 12, peer, this.inputs, jobs);
				addJob(nextJobId++, 32, 15, peer, this.inputs, jobs);
			}

		};

		JobSchedulerPolicy jobScheduler = new OurGridPersistentScheduler(peers);

		Grid grid = new Grid(peers);
		
		oursim = new OurSim(EventQueue.getInstance(), grid, jobScheduler, workload, availability);
		oursim.setActiveEntity(new ActiveEntityImp());
		oursim.start();

		// um dos jobs não vai ser completado por indisponiblidade de máquina.
		int numberOfFinishedJobs = jobs.size() - 1;
		int numberOfFinishedTasks = numberOfFinishedJobs;
		assertEquals(numberOfFinishedJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(11, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(numberOfFinishedTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(11, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());

	}

}