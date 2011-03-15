package br.edu.ufcg.lsd.oursim.policy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * @author Matheus G. do Rêgo, matheusgr@lsd.ufcg.edu.br
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class NoFSharingPolicy implements ResourceSharingPolicy {

	private Map<Peer, HashMap<Peer, Long>> allBalances;

	private static NoFSharingPolicy instance = null;

	/**
	 * NoFComparator order Peers using the NoF sharing policy. Peers with a
	 * greater share of remote resources come first.
	 */
	private class NoFComparator implements Comparator<Peer> {

		private Peer provider;
		private Set<Task> runningTasks;
		private Map<Peer, Integer> resourcesBeingConsumed;

		public NoFComparator(Peer provider, Map<Peer, Integer> resourcesBeingConsumed, Set<Task> runningElements) {
			this.provider = provider;
			this.resourcesBeingConsumed = resourcesBeingConsumed;
			this.runningTasks = runningElements;
		}

		@Override
		public int compare(Peer peer1, Peer peer2) {

			// If comparing oneself
			if (peer1 == peer2) {
				return 0;
			}

			// Local peer first (never preempt from yourself)
			if (peer1 == provider) {
				return -4;
			}

			// Best balance first
			long balanceDiff = getBalance(provider, peer1) - getBalance(provider, peer2);

			if (balanceDiff != 0) {
				return balanceDiff > 0 ? -3 : 3;
			}

			// Peers with same balance but using less resources first
			int p1Consumer = resourcesBeingConsumed.containsKey(peer1) ? resourcesBeingConsumed.get(peer1) : 0;

			int p2Consumer = resourcesBeingConsumed.containsKey(peer2) ? resourcesBeingConsumed.get(peer2) : 0;

			int usingDiff = p1Consumer - p2Consumer;

			if (usingDiff != 0) {
				return usingDiff > 0 ? -2 : 2;
			}

			assert (p2Consumer != 0 || p1Consumer != 0);

			// If peers share the same balance and same resource use, put the
			// peer with the most recently job last
			long mostRecentlyStartTime = -1;
			Peer p = peer1;

			for (Task Task : runningTasks) {
				if (Task.getSourcePeer() == peer1 && Task.getStartTime() > mostRecentlyStartTime) {
					p = peer1;
					mostRecentlyStartTime = Task.getStartTime();
				} else if (Task.getSourcePeer() == peer2 && Task.getStartTime() > mostRecentlyStartTime) {
					p = peer2;
					mostRecentlyStartTime = Task.getStartTime();
				}
			}
			return p == peer1 ? 1 : -1;
		}
	}

	private NoFSharingPolicy() {
		allBalances = new HashMap<Peer, HashMap<Peer, Long>>();
	}

	public static NoFSharingPolicy getInstance() {
		return instance = (instance != null) ? instance : new NoFSharingPolicy();
	}

	@Override
	public void addPeer(Peer peer) {
		this.allBalances.put(peer, new HashMap<Peer, Long>());
	}

	private void updateBalance(Peer provider, Peer consumer, long balance) {

		HashMap<Peer, Long> balances = allBalances.get(provider);
		long finalBalance = getBalance(consumer, balances) + balance;
		if (finalBalance < 0) {
			balances.remove(consumer);
		} else {
			balances.put(consumer, finalBalance);
		}
	}

	@Override
	public void increaseBalance(Peer source, Peer target, Task Task) {
		assert target != source;
		if (Task.isFinished()) {
			updateBalance(source, target, Task.getRunningTime());
		}

	}

	@Override
	public void decreaseBalance(Peer source, Peer target, Task Task) {
		if (Task.isFinished()) {
			updateBalance(source, target, -Task.getRunningTime());
		}
	}

	@Override
	public void updateMutualBalance(Peer provider, Peer consumer, Task Task) {
		// Don't update the balance to itself
		if (provider != consumer) {
			decreaseBalance(provider, consumer, Task);
			increaseBalance(consumer, provider, Task);
		}
	}

	@Override
	public long getBalance(Peer source, Peer target) {
		assert allBalances.containsKey(source);
		HashMap<Peer, Long> balances = allBalances.get(source);
		return getBalance(target, balances);
	}

	private long getBalance(Peer consumer, HashMap<Peer, Long> balances) {
		return balances.containsKey(consumer) ? balances.get(consumer) : 0;
	}

	@Override
	public List<Peer> getPreemptablePeers(final Peer provider, Peer consumer, final Map<Peer, Integer> resourcesBeingConsumed, final Set<Task> runningTasks) {
		List<Peer> peersByPriorityOfPreemption = sortPeersByPriorityOfPreemption(provider, consumer, resourcesBeingConsumed, runningTasks);
		peersByPriorityOfPreemption.remove(consumer);
		return peersByPriorityOfPreemption;
	}

	private List<Peer> sortPeersByPriorityOfPreemption(final Peer provider, Peer consumer, final Map<Peer, Integer> resourcesBeingConsumed,
			final Set<Task> runningTasks) {
		assert allBalances.containsKey(provider);

		// Provider's balance
		HashMap<Peer, Long> balances = allBalances.get(provider);

		// Comparing who much each peer deserves. Ordering made by
		// NoFComparator.
		TreeMap<Peer, Integer> resourcesBeingConsumedClone = new TreeMap<Peer, Integer>(new NoFComparator(provider, resourcesBeingConsumed, runningTasks));
		resourcesBeingConsumedClone.putAll(resourcesBeingConsumed);

		// If this peer is not already consuming, put in this map
		if (!resourcesBeingConsumedClone.containsKey(consumer)) {
			resourcesBeingConsumedClone.put(consumer, 1);
		} else {
			resourcesBeingConsumedClone.put(consumer, resourcesBeingConsumedClone.get(consumer) + 1);
		}

		long resourcesLeft = provider.getNumberOfMachinesToShare();

		// XXX && !resourcesBeingConsumedClone.isEmpty()
		while (resourcesLeft > 0 && !resourcesBeingConsumedClone.isEmpty()) {

			// Consumers to share resources left by providing peer
			int numConsumingPeers = resourcesBeingConsumedClone.size();

			// Sum of balances
			int totalBalance = 0;

			for (Peer remoteConsumer : resourcesBeingConsumedClone.keySet()) {
				totalBalance += getBalance(remoteConsumer, balances);
			}

			// Store resources received during this share
			HashMap<Peer, Long> receivedResources = new HashMap<Peer, Long>();

			long resourcesToShare = resourcesLeft;

			// If all peers deserves only less that one resource, start lenient
			// strategy
			boolean startLenientSharing = true;

			// Set minimum resources allowed for each peer and remove satisfied
			// consumers for the sharing of resources left
			for (Iterator<Peer> iterator = resourcesBeingConsumedClone.keySet().iterator(); iterator.hasNext();) {
				Peer remoteConsumer = iterator.next();

				// Amount of resources received
				long resourcesForPeer = 0;

				// Allowed share
				double share;

				if (totalBalance == 0) {
					// All peers doesn't have shared any resources
					share = (1.0d / numConsumingPeers);
				} else {
					share = ((double) getBalance(remoteConsumer, balances)) / totalBalance;
				}

				resourcesForPeer = (int) (share * resourcesToShare);

				if (remoteConsumer == provider) {
					// If consumer is provider, he has all resources left
					resourcesForPeer = resourcesLeft;
				}

				// If any peer cannot get at least one resource, start lenient
				// sharing
				startLenientSharing = startLenientSharing && resourcesForPeer == 0;

				// XXX ? resourcesBeingConsumedClone.get(remoteConsumer) : 0
				int resourcesInUse = resourcesBeingConsumedClone.containsKey(remoteConsumer) ? resourcesBeingConsumedClone.get(remoteConsumer) : 0;

				if (resourcesInUse <= resourcesForPeer) {
					iterator.remove(); // Satisfied consumer, don't preempt
				} else {
					receivedResources.put(remoteConsumer, resourcesForPeer);
				}

				resourcesLeft -= Math.min(resourcesInUse, resourcesForPeer);
			}

			// If lenient, distributed remain resources according NoF ordering
			if (startLenientSharing) {
				for (Peer p : resourcesBeingConsumedClone.keySet()) {
					if (resourcesLeft == 0) {
						break;
					}
					receivedResources.put(p, 1l);
					resourcesLeft--;
				}
			}

			// Recalculating resources consumed by this peer
			// This is used to recalculating a new allowed shared in next turn
			for (Entry<Peer, Long> entry : receivedResources.entrySet()) {
				//XXX ?resourcesBeingConsumedClone.get(entry.getKey()):0
				long currentUsedResources = resourcesBeingConsumedClone.containsKey(entry.getKey())?resourcesBeingConsumedClone.get(entry.getKey()):0;
				long allowedResources = entry.getValue();
				long finalResourceUse = currentUsedResources - allowedResources;
				assert finalResourceUse >= 0;
				if (finalResourceUse == 0) {
					resourcesBeingConsumedClone.remove(entry.getKey());
				} else {
					resourcesBeingConsumedClone.put(entry.getKey(), (int) (finalResourceUse));
				}
			}

		}

		// Consumer will not get any resource from this provider
		if (resourcesBeingConsumedClone.containsKey(consumer)) {
			return new ArrayList<Peer>();
		}

		return new ArrayList<Peer>(resourcesBeingConsumedClone.keySet());

	}

}
