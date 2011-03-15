package br.edu.ufcg.lsd.oursim.entities.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * Responsible for manage the resources belonged to a {@link Peer}, holding the
 * status of the resources.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 14/05/2010
 * 
 */
public class ResourceManager {

	/**
	 * The peer where the tasks are executing.
	 */
	private Peer peer;

	private Map<String, Machine> allocated;

	private Map<String, Machine> free;

	private Map<String, Machine> unavailable;

	public ResourceManager(Peer peer) {

		this.peer = peer;

		this.free = new HashMap<String, Machine>();
		this.allocated = new HashMap<String, Machine>();
		this.unavailable = new HashMap<String, Machine>();

		for (Machine machine : this.peer.getMachines()) {
			this.unavailable.put(machine.getName(), machine);
		}

	}

	public Machine allocateResource() {
		assert this.hasAvailableResource();
		Iterator<Machine> it = free.values().iterator();
		Machine chosen = it.next();
		it.remove();
		this.allocated.put(chosen.getName(), chosen);
		return chosen;
	}

	public Machine allocateResourceToTask(Task Task) {
		assert this.hasAvailableResource();
		LinkedList<Machine> freeResources = new LinkedList<Machine>(free.values());
		Task.prioritizeResourcesToConsume(freeResources);
		// TODO: considerar o caso em que nenhuma máquina satisfaz a task.
		Machine chosen = freeResources.getFirst();
		assert this.free.containsValue(chosen);
		this.free.remove(chosen.getName());
		this.allocated.put(chosen.getName(), chosen);
		return chosen;
	}

	public void releaseResource(Machine resource) {
		assert resource != null;
		this.releaseResource(resource.getName());
	}

	public void releaseResource(String machineName) {
		assert this.allocated.containsKey(machineName) : machineName;
		Machine resource = this.allocated.remove(machineName);
		assert !this.allocated.containsKey(machineName);
		this.free.put(resource.getName(), resource);
	}

	public void makeResourceUnavailable(String machineName) {
		assert this.allocated.containsKey(machineName) || this.free.containsKey(machineName);
		Machine resource = this.allocated.containsKey(machineName) ? this.allocated.remove(machineName) : this.free.remove(machineName);
		assert resource.getName().equals(machineName) : resource.getName() + " == " + machineName;
		//XXX deu um nullpointerexception nessa próxima linha quando estava usando o gerador de disponibilidade de mutka
		this.unavailable.put(resource.getName(), resource);
	}

	public void makeResourceAvailable(String machineName) {
		assert this.unavailable.containsKey(machineName) : machineName;
		Machine resource = this.unavailable.remove(machineName);
		this.free.put(machineName, resource);
	}

	public void makeResourceAllocated(Machine resource) {
		assert this.free.containsValue(resource);
		assert !this.allocated.containsValue(resource);
		this.free.remove(resource.getName());
		this.allocated.put(resource.getName(), resource);
	}

	public boolean isAllocated(String machineName) {
		return this.allocated.containsKey(machineName);
	}

	public boolean isAllocated(Machine machine) {
		return isAllocated(machine.getName());
	}

	public boolean hasAvailableResource() {
		return !this.free.isEmpty();
	}

	public int getNumberOfAvailableResources() {
		return this.free.size();
	}

	public int getNumberOfAllocatedResources() {
		return this.allocated.size();
	}

	public int getNumberOfUnavailableResources() {
		return this.unavailable.size();
	}

	public int getNumberOfResources() {
		return this.peer.getNumberOfMachines();
	}

	public Machine getResource(String machineName) {
		assert this.free.containsKey(machineName) || this.allocated.containsKey(machineName) || this.unavailable.containsKey(machineName);
		if (this.free.containsKey(machineName)) {
			return this.free.get(machineName);
		} else if (this.allocated.containsKey(machineName)) {
			return this.allocated.get(machineName);
		} else {
			return this.unavailable.get(machineName);
		}
	}

	public boolean hasResource(String machineName) {
		return this.free.containsKey(machineName) || this.allocated.containsKey(machineName) || this.unavailable.containsKey(machineName);
	}

	/**
	 * Indicates that there are new resources added to the peer, so, this
	 * ResourceManager must update its internal date structures to make sense of
	 * this resource addition.
	 */
	public void update() {
		for (Machine machine : this.peer.getMachines()) {
			update(machine);
		}
	}

	/**
	 * Indicates that there are a new resource added to the peer, so, this
	 * ResourceManager must update its internal date structures to make sense of
	 * this resource addition.
	 * 
	 * @param machine
	 *            The machine that was added to the peer.
	 */
	public void update(Machine machine) {
		// update if no one of the collection holds the machine.
		if (!this.unavailable.containsValue(machine) && !this.allocated.containsValue(machine) && !this.free.containsValue(machine)) {
			this.unavailable.put(machine.getName(), machine);
		}
	}

}