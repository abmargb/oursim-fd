package br.edu.ufcg.lsd.oursim.policy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import br.edu.ufcg.lsd.oursim.AbstractOurSimAPITest;
import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.Grid;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.io.input.workload.WorkloadAbstract;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityImp;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;

public class OurGridPersistentSchedulerTest extends AbstractOurSimAPITest {

	/**
	 * Cenário de Teste: Todos os peers possuem uma demanda que casa
	 * perfeitamente com seus recursos. Ninguém precisa recorrer aos recursos
	 * alheios. As submissões são feitas de forma persistente, isto é, na
	 * presença de preemptação é realizada a imediata ressubmissão da task.
	 * 
	 * Asserção: Espera-se que todos os jobs sejam concluídos no tempo mínimo
	 * necessário (i.e. a duração especificada para cada job) e que todos os
	 * jobs sejam executados no próprio peer de origem.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRun_1() throws Exception {
		// Define as demandas para cada peer
		Workload workload = generateDefaultWorkload();

		// Define os eventos de disponibilidade para cada recurso de cada peer.
		// Nesse cenário os recursos ficarão disponíveis o tempo suficiente para
		// terminar as demandas de cada job.
		Input<AvailabilityRecord> availability = new DedicatedResourcesAvailabilityCharacterization(peers, JOB_SUBMISSION_TIME, JOB_LENGTH + 1);

		JobSchedulerPolicy jobScheduler = new OurGridPersistentScheduler(peers);
		oursim = new OurSim(EventQueue.getInstance(), new Grid(peers), jobScheduler, workload, availability);
		oursim.setActiveEntity(new ActiveEntityImp());
		oursim.start();

		int totalDeTasks = TOTAL_OF_JOBS * NUMBER_OF_TASKS_BY_JOB;
		int totalDeWorkers = NUMBER_OF_PEERS * NUMBER_OF_RESOURCES_BY_PEER;

		int totalDeJobEvents = TOTAL_OF_JOBS * jobEvents.size();
		int totalDeTaskEvents = totalDeTasks * taskEvents.size();
		int totalDeWorkerEvents = totalDeWorkers * workerEvents.size();

		int totalDeEventos = totalDeJobEvents + totalDeTaskEvents + totalDeWorkerEvents;

		assertEquals(TOTAL_OF_JOBS, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(totalDeTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());
		assertEquals(totalDeEventos, EventQueue.totalNumberOfEvents);

		for (Job job : jobs) {
			// Espera-se que todos os jobs sejam concluídos no tempo mínimo
			// esperado (i.e. a duração especificado para cada job) e que todos
			// os jobs sejam executados no próprio peer de origem.
			assertEquals((JOB_SUBMISSION_TIME + JOB_LENGTH), (long) job.getFinishTime());
			// Ninguém precisa recorrer aos recursos alheios.
			assertEquals(job.getSourcePeer(), job.getTargetPeers().get(0));
		}
	}

	/**
	 * Cenário de Teste: Metade dos peers possue uma demanda que casa
	 * perfeitamente com seus recursos. A outra metade possui uma demanda duas
	 * vezes maior do que sua quantidade de recursos.
	 * 
	 * Espera-se que todos os jobs de quem não depende de recursos alheios sejam
	 * concluídos no tempo mínimo esperado (i.e. a duração especificado para
	 * cada job) e que todos os jobs sejam executados no próprio peer de origem.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRun_2() throws Exception {
		final int NUMBER_OF_OVERLOADED_PEERS = NUMBER_OF_PEERS / 2;

		final ArrayList<Peer> overloadedPeers = new ArrayList<Peer>(NUMBER_OF_OVERLOADED_PEERS);

		Workload defaultWorkload = generateDefaultWorkload();

		// workload extra, responsável por gerar sobrecarga em alguns dos peers
		WorkloadAbstract workload = new WorkloadAbstract() {

			@Override
			protected void setUp() {
				// Atribuir à metade dos peers uma demanda duas vezes maior do
				// que sua quantidade de recursos.
				for (int k = 0; k < NUMBER_OF_OVERLOADED_PEERS; k++) {
					Peer peer = peers.get(k);
					overloadedPeers.add(peer);
					for (int i = 0; i < NUMBER_OF_JOBS_BY_PEER; i++) {
						Job job = new Job(nextJobId, JOB_SUBMISSION_TIME, peer);
						for (int j = 0; j < NUMBER_OF_TASKS_BY_JOB; j++) {
							job.addTask("", JOB_LENGTH);
						}
						this.inputs.add(job);
						nextJobId++;
						jobs.add(job);
					}
				}
			}

		};

		// workload final: default + o extra, que vai gerar sobrecarga e, por
		// isso, espera em fila.
		workload.merge(defaultWorkload);

		// Define os eventos de disponibilidade para cada recurso de cada peer.
		// Nesse cenário os recursos ficarão disponíveis o tempo suficiente para
		// terminar as demandas de cada job.

		Input<AvailabilityRecord> availability = new DedicatedResourcesAvailabilityCharacterization(peers, JOB_SUBMISSION_TIME, (JOB_LENGTH * 2)+1);

		JobSchedulerPolicy jobScheduler = new OurGridPersistentScheduler(peers);

		oursim = new OurSim(EventQueue.getInstance(), new Grid(peers), jobScheduler, workload, availability);
		oursim.setActiveEntity(new ActiveEntityImp());
		oursim.start();

		int totalDeJobs = (int) (TOTAL_OF_JOBS * 1.5);
		int totalDeTasks = totalDeJobs * NUMBER_OF_TASKS_BY_JOB;
		int totalDeWorkers = NUMBER_OF_PEERS * NUMBER_OF_RESOURCES_BY_PEER;

		int totalDeJobEvents = totalDeJobs * jobEvents.size();
		int totalDeTaskEvents = totalDeTasks * taskEvents.size();
		int totalDeWorkerEvents = totalDeWorkers * workerEvents.size();

		int totalDeEventos = totalDeJobEvents + totalDeTaskEvents + totalDeWorkerEvents;

		assertEquals(totalDeJobs, this.jobEventCounter.getNumberOfFinishedJobs());
		assertEquals(0, this.jobEventCounter.getNumberOfPreemptionsForAllJobs());
		assertEquals(totalDeTasks, this.taskEventCounter.getNumberOfFinishedTasks());
		assertEquals(0, this.taskEventCounter.getNumberOfPreemptionsForAllTasks());
		assertEquals(totalDeEventos, EventQueue.totalNumberOfEvents);

		int numberOfJobsFromOverloadedPeersTimelyFinished = 0;
		int numberOfEnqueuedJobsFromOverloadedPeers = 0;
		for (Job job : jobs) {
			if (overloadedPeers.contains(job.getSourcePeer())) {
				// se teminou no tempo certo só pode ser porque executou no
				// próprio peer origem, visto que todos os outros estão cheios
				// com suas próprias demandas
				if (job.getFinishTime() == (JOB_SUBMISSION_TIME + JOB_LENGTH)) {
					assertEquals(job.getSourcePeer(), job.getTargetPeers().get(0));
					numberOfJobsFromOverloadedPeersTimelyFinished++;
				} else {
					numberOfEnqueuedJobsFromOverloadedPeers++;
				}
			} else {
				// Espera-se que esses jobs sejam concluídos no tempo mínimo
				// esperado (i.e. a duração especificado para cada job) e que
				// todos os jobs sejam executados no próprio peer de origem.
				assertEquals((JOB_SUBMISSION_TIME + JOB_LENGTH), (long) job.getFinishTime());
				// Nenhum desses jobs precisa recorrer a recursos alheios.
				assertEquals(job.getSourcePeer(), job.getTargetPeers().get(0));
			}
		}

		assertEquals(NUMBER_OF_OVERLOADED_PEERS * NUMBER_OF_JOBS_BY_PEER, numberOfJobsFromOverloadedPeersTimelyFinished);
		assertEquals(NUMBER_OF_OVERLOADED_PEERS * NUMBER_OF_JOBS_BY_PEER, numberOfEnqueuedJobsFromOverloadedPeers);
	}
}