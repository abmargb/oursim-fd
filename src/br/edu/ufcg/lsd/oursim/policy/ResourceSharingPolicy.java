package br.edu.ufcg.lsd.oursim.policy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;


/**
 * 
 * A policy to determine how the resources will be shared between the
 * participating peers in the grid.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public interface ResourceSharingPolicy {

	void addPeer(Peer peer);

	long getBalance(Peer source, Peer target);

	void increaseBalance(Peer source, Peer target, Task Task);

	void decreaseBalance(Peer source, Peer target, Task Task);

	void updateMutualBalance(Peer provider, Peer consumer, Task Task);

	/**
	 * Gets an collection of peers sorted in a way that prioritize the peer with
	 * better balances, that is,
	 * 
	 * @param resourcesBeingConsumed
	 *            a quantidade de recursos que cada peer remoto está consumindo
	 *            neste site
	 * @param runningTasks
	 *            todas as tasks não locais que estão rodando neste site
	 */
	List<Peer> getPreemptablePeers(Peer provider, Peer consumer, Map<Peer, Integer> resourcesBeingConsumed, Set<Task> runningTasks);

}