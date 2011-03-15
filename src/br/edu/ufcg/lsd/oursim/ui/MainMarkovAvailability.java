package br.edu.ufcg.lsd.oursim.ui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.availability.MarkovModelAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

public class MainMarkovAvailability {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Map<String, Peer> peersMap = new HashMap<String, Peer>();
		for (int i = 0; i < 10; i++) {
			Peer peer = new Peer("peer_" + i, 5, FifoSharingPolicy.getInstance());
			peersMap.put(peer.getName(), peer);
		}
		MarkovModelAvailabilityCharacterization availability = new MarkovModelAvailabilityCharacterization(peersMap, TimeUtil.ONE_WEEK, 0);

		availability.setBuffer(new BufferedWriter(new FileWriter("/home/edigley/local/traces/oursim/r_scripts/availability/oursim_worker_events_mutka.txt")));

		while (availability.peek() != null) {
			availability.poll();
		}
		
		availability.close();
	}
}
