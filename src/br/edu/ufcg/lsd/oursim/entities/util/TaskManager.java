package br.edu.ufcg.lsd.oursim.entities.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.util.BidirectionalMap;


/**
 * 
 * A helper class to deals with tasks in execution.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class TaskManager {

	/**
	 * The peer where the tasks are executing.
	 */
	private Peer peer;

	/**
	 * mapping between the tasks that are executing and the respective machine.
	 */
	private BidirectionalMap<Task, Machine> tasksInExecution;

	/**
	 * The collection of local tasks that are running, that is, the tasks that
	 * are running and belongs to {@link #peer}.
	 */
	private Set<Task> localTasks;

	/**
	 * The collection of foreign tasks that are running, that is, the tasks that
	 * are running and doesn't belong to {@link #peer}.
	 */
	private Set<Task> foreignTasks;

	/**
	 * 
	 * An ordinary constructor.
	 * 
	 * @param peer
	 *            The peer which holds the resources where the tasks running.
	 */
	public TaskManager(Peer peer) {
		this.peer = peer;
		this.tasksInExecution = new BidirectionalMap<Task, Machine>();
		this.foreignTasks = new HashSet<Task>();
		this.localTasks = new HashSet<Task>();
	}

	/**
	 * The collection of foreign tasks that are running, that is, the tasks that
	 * are running and doesn't belong to {@link #peer}.
	 * 
	 * @return The collection of foreign running tasks
	 */
	public Set<Task> getForeignTasks() {
		return this.foreignTasks;
	}

	/**
	 * The collection of local tasks that are running, that is, the tasks that
	 * are running and belongs to {@link #peer}.
	 * 
	 * @return The collection of local running tasks.
	 */
	public Set<Task> getLocalTasks() {
		return this.localTasks;
	}

	/**
	 * Adds a running tasks. This means that the {@link Task} <code>task</code>
	 * are running in a {@link Machine} <code>machine</code>.
	 * 
	 * @param Task
	 *            the task to be added.
	 * @param resource
	 *            the machine in which the task are running.
	 */
	public void startTask(Task Task, Machine resource) {
		assert resource != null && !tasksInExecution.containsKey(Task);

		this.tasksInExecution.put(Task, resource);

		Peer sourcePeer = Task.getSourcePeer();
		if (sourcePeer == peer) {
			this.addLocalTask(Task);
		} else {
			this.addForeignTask(Task);
		}

	}

	/**
	 * Finishs the task that are running in a given machine.
	 * 
	 * @param resource
	 *            the given machine.
	 */
	public void finishTask(Machine resource) {
		assert this.tasksInExecution.containsValue(resource);
		Task Task = this.getTask(resource);
		finishTask(Task);
	}

	/**
	 * Finishs a given task.
	 * 
	 * @param Task
	 *            the given task.
	 * @return the machine in which the given task was running.
	 */
	public Machine finishTask(Task Task) {
		assert this.tasksInExecution.containsKey(Task) : Task;
		Machine machine = this.tasksInExecution.remove(Task);
		boolean removed = this.remove(Task);
		assert removed && machine != null;
		return machine;
	}

	/**
	 * Remove a task from this taskManager.
	 * 
	 * @param Task
	 *            the task to be removed.
	 * @return <code>true</code> if the task has been successfully removed,
	 *         <code>false</false> otherwise.
	 */
	private boolean remove(Task Task) {
		return (Task.getSourcePeer() == peer) ? removeLocalTask(Task) : removeForeignTask(Task);
	}

	/**
	 * Gets all tasks that are running and belongs to a given peer.
	 * 
	 * @param chosen
	 *            the given peer.
	 * @return all tasks that are running and belongs to the given peer.
	 */
	public List<Task> getAllTasksFromPeer(Peer chosen) {
		List<Task> Tasks;
		if (chosen == peer) {
			Tasks = new ArrayList<Task>(localTasks);
		} else {
			// todas as tasks do escolhido que estão rodando
			Tasks = new LinkedList<Task>();
			for (Task j : foreignTasks) {
				if (j.getSourcePeer() == chosen) {
					Tasks.add(j);
				}
			}
		}
		return Tasks;
	}

	/**
	 * Gets all the running tasks of this taskManager.
	 * 
	 * @return all the running tasks of this taskManager.
	 */
	public Set<Task> getRunningTasks() {
		return this.tasksInExecution.keySet();
	}

	/**
	 * Checks if there are some foreign task running in the peer of this
	 * TaskManager.
	 * 
	 * @return <code>true</code> if there are some foreign task running,
	 *         <code>false</false> otherwise.
	 */
	public boolean hasForeignTask() {
		return !this.foreignTasks.isEmpty();
	}

	/**
	 * Gets the total of local running tasks, that is, that tasks thar are
	 * running and belong to the peer that holds this taskManager.
	 * 
	 * @return Gets the total of resources being locally consumed
	 */
	public int getNumberOfLocalTasks() {
		return this.localTasks.size();
	}

	/**
	 * Gets the machine in which a given task are running.
	 * 
	 * @param Task
	 *            the given task.
	 * @return the machine in which the given task are running.
	 */
	public Machine getMachine(Task Task) {
		return this.tasksInExecution.get(Task);
	}

	/**
	 * Gets the task that are running in a given machine.
	 * 
	 * @param resource
	 *            the given machine.
	 * @return the task that are running in a given machine.
	 */
	public Task getTask(Machine resource) {

		return this.tasksInExecution.getKey(resource);
	}

	/**
	 * Checks if there are some task running in a given machine.
	 * 
	 * @param machine
	 *            the given machine.
	 * @return <code>true</code> if there are some foreign task running in the
	 *         given machine, <code>false</false> otherwise.
	 */
	public boolean isInExecution(Machine machine) {
		return this.tasksInExecution.containsValue(machine);
	}

	/**
	 * Checks if a given task is running.
	 * 
	 * @param Task
	 *            the given task.
	 * @return <code>true</code> if the given task is running,
	 *         <code>false</false> otherwise.
	 */
	public boolean isInExecution(Task Task) {
		return this.tasksInExecution.containsKey(Task);
	}

	private void addLocalTask(Task Task) {
		assert !this.localTasks.contains(Task);
		this.localTasks.add(Task);
	}

	private void addForeignTask(Task Task) {
		this.foreignTasks.add(Task);
	}

	private boolean removeLocalTask(Task Task) {
		assert this.localTasks.contains(Task);
		return this.localTasks.remove(Task);
	}

	private boolean removeForeignTask(Task Task) {
		assert this.foreignTasks.contains(Task);
		return this.foreignTasks.remove(Task);
	}

}
