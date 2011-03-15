package br.edu.ufcg.lsd.oursim.io.input.availability;

import java.util.Collection;
import java.util.PriorityQueue;

import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;


/**
 * 
 * An AvailabilityCharacterization intended to, at least through a given period,
 * dedicated resources.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 25/05/2010
 * 
 */
public class DedicatedResourcesAvailabilityCharacterization extends InputAbstract<AvailabilityRecord> {

	/**
	 * An convenient constructor to make all the resources off all peers in
	 * <code>peers</code> available thorough the entire simulation.
	 * 
	 * @param peers
	 *            the peers from which the resources will be make available.
	 */
	public DedicatedResourcesAvailabilityCharacterization(Collection<Peer> peers) {
		this(peers, 0, Long.MAX_VALUE);
	}

	/**
	 * Prepares all the resources of all peers in <code>peers</code> to be
	 * available for <code>duration</code> units of simulations since
	 * <code>timestamp</code>.
	 * 
	 * @param peers
	 *            the peers from which the resources will be make available.
	 * @param timestamp
	 *            the initial time from which the resources will be make
	 *            available.
	 * @param duration
	 *            the duration of the availability period.
	 */
	public DedicatedResourcesAvailabilityCharacterization(Collection<Peer> peers, long timestamp, long duration) {
		this.inputs = new PriorityQueue<AvailabilityRecord>();

		for (Peer peer : peers) {
			for (Machine machine : peer.getMachines()) {
				this.inputs.add(new AvailabilityRecord(machine.getName(), timestamp, duration));
			}
		}
	}

}