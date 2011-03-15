package br.edu.ufcg.lsd.oursim.policy;

import java.util.Iterator;
import java.util.List;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * An implementation of a {@link JobSchedulerPolicy} that persistently resubmits
 * the tasks that have been preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class OurGridPersistentScheduler extends JobSchedulerPolicyAbstract {

	/**
	 * An ordinary constructor.
	 * 
	 * @param peers
	 *            All the peers that compound of the grid.
	 */
	public OurGridPersistentScheduler(List<Peer> peers) {
		super(peers);
	}

	@Override
	public final void schedule() {
		for (Iterator<Task> iterator = this.getSubmittedTasks().iterator(); iterator.hasNext();) {
			Task Task = iterator.next();
			Task.getSourcePeer().prioritizePeersToConsume(this.getPeers());
			for (Peer provider : this.getPeers()) {
				boolean isTaskRunning = provider.executeTask(Task);
				if (isTaskRunning) {
					iterator.remove();
					break;
				}
			}
		}
	}

	@Override
	public final void taskPreempted(Event<Task> taskEvent) {
		super.taskPreempted(taskEvent);
		Task Task = taskEvent.getSource();
		this.rescheduleTask(Task);
	}

}
