package br.edu.ufcg.lsd.oursim.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * An ordinary FIFO sharing policy, that is, there are no possibility of
 * preemption of a task in behalf of another. In this policy there are
 * preemptios only on behalf o the local tasks.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public final class FifoSharingPolicy implements ResourceSharingPolicy {

	private static FifoSharingPolicy instance = null;

	private FifoSharingPolicy() {
	}

	public static FifoSharingPolicy getInstance() {
		return instance = (instance != null) ? instance : new FifoSharingPolicy();
	}

	@Override
	public List<Peer> getPreemptablePeers(Peer provider, Peer consumer, Map<Peer, Integer> resourcesBeingConsumed, Set<Task> runningTasks) {
		ArrayList<Peer> preemptablePeers = new ArrayList<Peer>();
		if (consumer.equals(provider)) {
			for (Peer peer : resourcesBeingConsumed.keySet()) {
				if (peer != provider) {
					preemptablePeers.add(peer);
				}
			}
		}
		assert !preemptablePeers.contains(provider);
		return preemptablePeers;
	}

	public long getBalance(Peer source, Peer target) {
		return Long.MAX_VALUE;
	}

	public void addPeer(Peer peer) {
	}

	public void increaseBalance(Peer source, Peer target, Task Task) {
	}

	public void decreaseBalance(Peer source, Peer target, Task Task) {
	}

	public void updateMutualBalance(Peer provider, Peer consumer, Task Task) {
	}
}
