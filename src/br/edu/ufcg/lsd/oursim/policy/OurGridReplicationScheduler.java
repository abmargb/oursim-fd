package br.edu.ufcg.lsd.oursim.policy;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * An implementation of a {@link JobSchedulerPolicy} that replies tasks
 * intending reduce the job's makespan.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class OurGridReplicationScheduler extends JobSchedulerPolicyAbstract {

	/**
	 * the level of replication of the tasks that comprise this job. A <i>value</i>
	 * less than or equal 1 means no replication. A <i>value</i> greater than 1
	 * means that <i>value</i> replicas will be created for each task.
	 */
	private int replicationLevel;

	/**
	 * An ordinary constructor.
	 * 
	 * @param peers
	 *            All the peers that compound of the grid.
	 * @param replicationLevel
	 *            the level of replication of the tasks that comprise this job.
	 *            A <i>value</i> less than or equal 1 means no replication. A
	 *            <i>value</i> greater than 1 means that <i>value</i> replies
	 *            will be created for each task.
	 */
	public OurGridReplicationScheduler(List<Peer> peers, int replicationLevel) {
		super(peers);
		assert replicationLevel > 0;
		this.replicationLevel = replicationLevel;
	}

	@Override
	public final void schedule() {
		for (Iterator<Task> iterator = this.getSubmittedTasks().iterator(); iterator.hasNext();) {
			Task Task = iterator.next();
			Task.getSourcePeer().prioritizePeersToConsume(this.getPeers());
			for (Peer provider : this.getPeers()) {
				boolean isTaskRunning = provider.executeTask(Task);
				if (isTaskRunning) {
					// System.out.println("Is Running: " + task.getStartTime() +
					// " : " + task.getId() + " : " + task.getReplicaId());
					iterator.remove();
					break;
				}
			}
		}
	}

	@Override
	public final void addJob(Job job) {
		assert !job.getTasks().isEmpty();
		job.setReplicationLevel(this.replicationLevel);
		// this.getSubmittedJobs().add(job);

		// adiciona a matriz das tasks
		for (Task Task : job.getTasks()) {
			this.getSubmittedTasks().add(Task);
		}

		// adiciona (replicationLevel-1) réplicas -> a matriz já foi adicionada
		addReplicas(job.getTasks(), job.getReplicationLevel() - 1);

	}

	@Override
	public final void taskFinished(Event<Task> taskEvent) {
		super.taskFinished(taskEvent);
		stopRemainingReplicas(taskEvent.getSource());
		taskEvent.getSource().finishSourceTask();
	}

	@Override
	public final void taskPreempted(Event<Task> taskEvent) {
		super.taskPreempted(taskEvent);
		this.getSubmittedTasks().add(taskEvent.getSource());
	}

	private void addReplicas(Collection<Task> Tasks, int numberOfReplicas) {
		for (int i = 0; i < numberOfReplicas; i++) {
			for (Task Task : Tasks) {
				this.getSubmittedTasks().add(Task.makeReplica());
			}
		}
	}

	private void stopRemainingReplicas(Task Task) {

		for (Task replica : Task.getReplicas()) {
			// para as replicas que ainda estiverem rodando
			if (replica.isRunning()) {
				assert this.getRunningTasks().contains(replica) : replica;
				if (replica.getEstimatedFinishTime() > Task.getFinishTime()) {
					// ocorria problema quando a task tinha acabado de iniciar
					assert this.getRunningTasks().contains(replica) || replica.getStartTime() == getCurrentTime() : getCurrentTime() + ": " + replica;
					assert !replica.isFinished();
					replica.getTargetPeer().cancelTask(replica);
				} else if (replica.getEstimatedFinishTime() == Task.getFinishTime()) {
					// Pode estar no caso "=" o warning apresentado
					replica.getTargetPeer().cancelTask(replica);
				} else {
					replica.getTargetPeer().cancelTask(replica);
				}
			} else if (replica.isCancelled()) {
				assert !this.getRunningTasks().contains(replica) : replica;
				assert !this.getSubmittedTasks().contains(replica) : replica;
			} else if (replica.wasPreempted()) {
				assert this.getSubmittedTasks().contains(replica) : replica;
				boolean removed = this.getSubmittedTasks().remove(replica);
				assert removed : replica;
			} else if (this.getSubmittedTasks().contains(replica)) {// está
				// aguardando?
				replica.cancel();
				boolean removed = this.getSubmittedTasks().remove(replica);
				assert removed : replica;
			} else {
				throw new RuntimeException("situação não esperada: " + replica);
			}
		}

	}

}
