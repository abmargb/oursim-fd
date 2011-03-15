package br.edu.ufcg.lsd.oursim.policy.ranking;

import java.util.Collections;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.util.Seed;

/**
 * 
 * A policy to prioritize the peers from which the resources will be consumed.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 */
public class PeerRankingPolicy extends RankingPolicy<Peer, Peer> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param peer
	 *            the peer who are are requesting.
	 */
	public PeerRankingPolicy(Peer peer) {
		super(peer);
	}

	@Override
	public void rank(List<Peer> peers) {
		// // Getting best balance first
		// Collections.sort(peers, new Comparator<Peer>() {
		// @Override
		// public int compare(Peer o1, Peer o2) {
		// return o2.getBalance(consumer)
		// - o1.getBalance(consumer);
		// }
		//
		// });
		Collections.shuffle(peers, Seed.PeerRankingPolicy_RANDOM);
		// Trying own resources first:
		for (int i = 0; i < peers.size(); i++) {
			if (peers.get(i) == this.getRequester()) {
				peers.set(i, peers.get(0));
				peers.set(0, this.getRequester());
				break;
			}
		}
		assert (peers.get(0) == this.getRequester());
	}

}
