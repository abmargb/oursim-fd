package br.edu.ufcg.lsd.oursim.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventListener;
import br.edu.ufcg.lsd.oursim.entities.util.ResourceAllocationManager;
import br.edu.ufcg.lsd.oursim.entities.util.ResourceManager;
import br.edu.ufcg.lsd.oursim.entities.util.TaskManager;
import br.edu.ufcg.lsd.oursim.policy.FifoSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.ResourceSharingPolicy;
import br.edu.ufcg.lsd.oursim.policy.ranking.PeerRankingPolicy;
import br.edu.ufcg.lsd.oursim.policy.ranking.TaskPreemptionRankingPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntityImp;

/**
 * 
 * Represents a peer in a Peer-to-Peer grid. A peer is a administrative domain
 * that holds and manages a collection of machines. The management is based in a
 * group of policies represented by {@link ResourceAllocationManager},
 * {@link ResourceSharingPolicy} and {@link ResourceRankingPolicy}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class Peer extends ActiveEntityImp implements WorkerEventListener {

	public static final Peer DEFAULT_PEER = new Peer("Default_Peer", FifoSharingPolicy.getInstance());

	/**
	 * The peer's name.
	 */
	private final String name;

	/**
	 * The collection of machines owned by this peer.
	 */
	private final List<Machine> machines;

	private final ResourceAllocationManager resourceAllocationManager;

	private final PeerRankingPolicy peerRankingPolicy;

	private final TaskPreemptionRankingPolicy taskPreemptionRankingPolicy;

	private final ResourceSharingPolicy resourceSharingPolicy;

	private final ResourceManager resourceManager;

	private final TaskManager taskManager;

	private Machine referenceMachine;

	// private long amountOfAvailableTime = 0;
	private Map<String, Long> amountOfAvailableTime;
	private Map<String, Long> amountOfWastedTime;
	private Map<String, Long> amountOfUsefulTime;
	private Map<String, Long> timeOfLastWorkAvailableEvent;

	/**
	 * All the jobs originated by this peer, that is, all the jobs that belongs
	 * to this peer. Unlike {@link #workload} the source of this collections
	 * must remais unchanged through the entire life of simulation.
	 */
	private List<Job> jobs;

	/**
	 * Field to assure the uniqueness of the id of each machine.
	 */
	private static long nextMachineId = 0;

	/**
	 * 
	 * An convenient constructs for peers that have only homogeneous machines,
	 * that is, the peer have <code>numberOfMachines</code> machines and each
	 * machine represents an reference machine.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param numberOfMachines
	 *            The number of machines that this peer manages.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the machines of this peers
	 *            with another ones.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfMachines &lt; 1</code>.
	 * 
	 */
	public Peer(String name, int numberOfMachines, ResourceSharingPolicy resourceSharingPolicy) {
		this(name, numberOfMachines, Processor.EC2_COMPUTE_UNIT.getSpeed(), resourceSharingPolicy);
	}

	/**
	 * 
	 * An convenient constructs for peers that have only homogeneous machines,
	 * that is, the peer have <code>numberOfMachines</code> machines and each
	 * machine has the same speed <code>nodeMIPSRating</code>.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param numberOfMachines
	 *            The number of machines that this peer manages.
	 * @param nodeMIPSRating
	 *            the mips rating of each machine of this peer.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the machines of this peers
	 *            with another ones.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfMachines &lt; 1</code>.
	 * 
	 */
	public Peer(String name, int numberOfMachines, long nodeMIPSRating, ResourceSharingPolicy resourceSharingPolicy) {
		this(name, resourceSharingPolicy);
		assert numberOfMachines > 0;
		if (numberOfMachines < 1) {
			throw new IllegalArgumentException("numberOfMachines must be at least 1.");
		}
		for (int i = 0; i < numberOfMachines; i++) {
			addMachine(nodeMIPSRating);
		}
	}

	/**
	 * 
	 * An generic constructor for a peer. After instantiated, it must be called
	 * the method {@link #addMachine(Machine)} to explicitly add the machines to
	 * this peer.
	 * 
	 * @param name
	 *            The peer's name.
	 * @param resourceSharingPolicy
	 *            the policy responsible for sharing the machines of this peers
	 *            with another ones.
	 */
	public Peer(String name, ResourceSharingPolicy resourceSharingPolicy) {

		this.name = name;

		this.resourceSharingPolicy = resourceSharingPolicy;
		this.resourceSharingPolicy.addPeer(this);

		this.jobs = new ArrayList<Job>();
		this.machines = new ArrayList<Machine>();

		this.taskManager = new TaskManager(this);

		this.peerRankingPolicy = new PeerRankingPolicy(this);

		this.resourceManager = new ResourceManager(this);
		this.resourceAllocationManager = new ResourceAllocationManager(this, this.resourceManager, this.taskManager);

		this.taskPreemptionRankingPolicy = new TaskPreemptionRankingPolicy(this);

		this.amountOfAvailableTime = new HashMap<String, Long>();
		this.amountOfWastedTime = new HashMap<String, Long>();
		this.amountOfUsefulTime = new HashMap<String, Long>();
		this.timeOfLastWorkAvailableEvent = new HashMap<String, Long>();

	}

	/**
	 * Create and adds a Machine based on the mipsRating.
	 * 
	 * @param nodeMIPSRating
	 *            the mips rating of the machine to be added.
	 */
	private final void addMachine(long nodeMIPSRating) {
		addMachine(new Machine(getMachineName(nextMachineId), nodeMIPSRating));
		nextMachineId++;
	}

	/**
	 * Adds a new machine to this peer.
	 * 
	 * @param machine
	 *            The machine to be added.
	 */
	public final void addMachine(Machine machine) {
		this.machines.add(machine);
		// as have been added machines after the instantiation of the
		// ResourceManager, this must be updated.
		this.resourceManager.update(machine);
		this.amountOfAvailableTime.put(machine.getName(), 0l);
		this.amountOfWastedTime.put(machine.getName(), 0l);
		this.amountOfUsefulTime.put(machine.getName(), 0l);
	}

	/**
	 * @return the name of this peer.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Update the status of all tasks being executed in the machines of this
	 * peer.
	 * 
	 * @param currentTime
	 *            The instante at which the update refers to.
	 */
	public void updateTime(long currentTime) {
		for (Task Task : this.taskManager.getRunningTasks()) {
			Long estimatedFinishTime = Task.getTaskExecution().updateProcessing(currentTime);
			if (estimatedFinishTime != null) {
				long finishTime = getCurrentTime() + estimatedFinishTime;
				this.addFinishTaskEvent(finishTime, Task);
			}
		}
	}

	/**
	 * @return the number of machines managed by this peer.
	 */
	public final int getNumberOfMachines() {
		return this.machines.size();
	}

	/**
	 * Only use machines that aren't busy by local jobs.
	 * 
	 * @return the number of machines that aren't busy by local jobs, and so
	 *         it's possible to share.
	 */
	public final long getNumberOfMachinesToShare() {
		// TODO: there are a bug here: it's needed to account the volatility of
		// the machines.
		// The right way: return
		// (this.resourceManager.getNumberOfAllocatedResources() +
		// this.resourceManager.getNumberOfAvailableResources())
		// - this.taskManager.getNumberOfLocalTasks();
		return this.resourceManager.getNumberOfResources() - this.taskManager.getNumberOfLocalTasks();

	}

	/**
	 * Adds a job to this peer. This means that this peer is the source of this
	 * job.
	 * 
	 * @param job
	 *            a job to be added to this peer.
	 * @return <code>true</code> if the job in fact belongs to this peer,
	 *         <code>false</code> otherwise.
	 */
	final boolean addJob(Job job) {
		assert job.getSourcePeer() == this;
		if (job.getSourcePeer() == this) {
			this.jobs.add(job);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add a task to be executed in this peer.
	 * 
	 * @param Task
	 *            The task to be executed.
	 * @return <code>true</code> if the task is succesfully added and is been
	 *         executed, <code>false</code> otherwise.
	 */
	public final boolean executeTask(Task Task) {
		Machine allocatedMachine = this.resourceAllocationManager.allocateTask(Task);
		if (allocatedMachine != null) {
			long currentTime = getCurrentTime();
			Processor defaultProcessor = allocatedMachine.getDefaultProcessor();
			Task.setTaskExecution(new TaskExecution(Task, defaultProcessor, currentTime));
			Task.setStartTime(currentTime);
			Task.setTargetPeer(this);
			this.addStartedTaskEvent(Task);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Finishs the execution of a task. This means the task has been succesfully
	 * executed and has been completed.
	 * 
	 * @param Task
	 *            The task to be finished.
	 * @throws IllegalArgumentException
	 *             if the task was not being executed in this peer.
	 */
	public final void finishTask(Task Task) throws IllegalArgumentException {
		assert this.taskManager.isInExecution(Task) : Task;

		String machineName = this.taskManager.getMachine(Task).getName();
		long usefulTime = getCurrentTime() - Task.getStartTime();

		if (this.taskManager.isInExecution(Task)) {
			this.resourceManager.releaseResource(this.taskManager.finishTask(Task));
			this.resourceSharingPolicy.updateMutualBalance(this, Task.getSourcePeer(), Task);
		} else {
			throw new IllegalArgumentException("The task was not being executed in this peer.");
		}

		Long cum = this.amountOfUsefulTime.get(machineName);
		this.amountOfUsefulTime.put(machineName, cum + usefulTime);
	}

	/**
	 * Preempts the execution of a task. This means the task was been executing
	 * but must be preempted, whatever the reason.
	 * 
	 * @param Task
	 *            The task to be preempted.
	 */
	public final void preemptTask(Task Task) {
		assert this.taskManager.isInExecution(Task) : Task;

		String machineName = this.taskManager.getMachine(Task).getName();
		long wastedTime = getCurrentTime() - Task.getStartTime();

		if (this.taskManager.isInExecution(Task)) {
			this.resourceAllocationManager.deallocateTask(Task);
			this.resourceSharingPolicy.updateMutualBalance(this, Task.getSourcePeer(), Task);
			// TODO XXX definir onde essas ações devem ficar: se em quem a
			// executa ou se no evento disparado.
			Task.preempt(getCurrentTime());
			this.addPreemptedTaskEvent(Task);
		} else {
			throw new IllegalArgumentException("The task was not being executed in this peer.");
		}

		Long cum = this.amountOfWastedTime.get(machineName);
		this.amountOfWastedTime.put(machineName, cum + wastedTime);

	}

	public final void cancelTask(Task Task) {
		assert this.taskManager.isInExecution(Task) : Task;

		if (this.taskManager.isInExecution(Task)) {
			String machineName = this.taskManager.getMachine(Task).getName();
			long wastedTime = getCurrentTime() - Task.getStartTime();

			this.resourceAllocationManager.deallocateTask(Task);
			this.resourceSharingPolicy.updateMutualBalance(this, Task.getSourcePeer(), Task);
			// TODO XXX definir onde essas ações devem ficar: se em quem a
			// executa ou se no evento disparado.
			Task.cancel();
			this.addCancelledTaskEvent(Task);

			Long cum = this.amountOfWastedTime.get(machineName);
			this.amountOfWastedTime.put(machineName, cum + wastedTime);
		} else {
			throw new IllegalArgumentException("The task " + Task + " was not being executed in this peer.");
		}

	}

	/**
	 * Gets the number of free machines.
	 * 
	 * @return the number of machines that are available to process tasks.
	 */
	public final int getNumberOfAvailableResources() {
		return this.resourceManager.getNumberOfAvailableResources();
	}

	public final int getNumberOfUnavailableResources() {
		return this.resourceManager.getNumberOfUnavailableResources();
	}

	/**
	 * @return the percentage of machines that are executing tasks.
	 */
	public final double getUtilization() {
		// return ((double) (this.getNumberOfMachines() -
		// this.getNumberOfAvailableResources())) / this.getNumberOfMachines();
		if (this.resourceManager.getNumberOfAvailableResources() == 0) {
			return 1;
		}
		return ((double) this.resourceManager.getNumberOfAllocatedResources())
				/ (this.resourceManager.getNumberOfAllocatedResources() + this.resourceManager.getNumberOfAvailableResources());
	}

	/**
	 * Gets the the machines managed by this peer.
	 * 
	 * @return the machines of this peer.
	 */
	public final List<Machine> getMachines() {
		return machines;
	}

	/**
	 * Verifies if this peer has the machine with the given name.
	 * 
	 * @param machineName
	 *            the name of the resource being queried.
	 * @return <code>true</code> if this peer has the machine with the given
	 *         name, <code>false</code> otherwise.
	 */
	public final boolean hasMachine(String machineName) {
		return this.resourceManager.hasResource(machineName);
	}

	// B-- beginning of implementation of SpotPriceEventListener

	@Override
	public final void workerAvailable(Event<String> workerEvent) {
		String machineName = workerEvent.getSource();
		this.resourceManager.makeResourceAvailable(machineName);
		// TODO: deve-se reescalonar os jobs agora pois tem recurso disponível
		this.timeOfLastWorkAvailableEvent.put(machineName, workerEvent.getTime());
	}

	@Override
	public final void workerUnavailable(Event<String> workerEvent) {
		String machineName = workerEvent.getSource();
		if (this.resourceManager.isAllocated(machineName)) {
			Machine resource = this.resourceManager.getResource(machineName);
			Task Task = this.taskManager.getTask(resource);
			preemptTask(Task);// TODO nesse momento o resource não estará mais
			// em
			// allocated e sim em free pois o preempt
			// efetivamente libera o recurso.
		}

		this.resourceManager.makeResourceUnavailable(machineName);

		assert this.amountOfAvailableTime.containsKey(machineName) : machineName;

		Long cum = this.amountOfAvailableTime.get(machineName);
		long timeElapsed = workerEvent.getTime() - this.timeOfLastWorkAvailableEvent.get(machineName);
		// if (timeElapsed > 5 * TimeUtil.ONE_MINUTE) {
		this.amountOfAvailableTime.put(machineName, cum + timeElapsed);
		// }
	}

	@Override
	public void workerUp(Event<String> workerEvent) {

	}

	@Override
	public void workerDown(Event<String> workerEvent) {
	}

	@Override
	public void workerIdle(Event<String> workerEvent) {
	}

	@Override
	public void workerRunning(Event<String> workerEvent) {
	}

	// E-- end of implementation of SpotPriceEventListener

	/**
	 * Sorts the collection of peer in a way that the preferable peers to
	 * consume are firstly accessed.
	 * 
	 * @param peers
	 *            the peers available to be consumed.
	 */
	public final void prioritizePeersToConsume(List<Peer> peers) {
		this.peerRankingPolicy.rank(peers);
	}

	/**
	 * Sorts the collection of tasks in a way that the preferable tasks to
	 * preemption are firstly accessed.
	 * 
	 * @param Tasks
	 *            the tasks candidates to preemption.
	 */
	public final void prioritizeTasksToPreemption(List<Task> Tasks) {
		this.taskPreemptionRankingPolicy.rank(Tasks);
	}

	/**
	 * Gets a collection of peers in a way that the preferable peers to
	 * preemption are firstly accessed.
	 * 
	 * @param consumer
	 *            the peer which will benefit from the preemption.
	 * @return the a collection of peers in a way that the preferable peers to
	 *         preemption are firstly accessed.
	 */
	public final List<Peer> prioritizePeersToPreemptionOnBehalfOf(Peer consumer) {
		Map<Peer, Integer> allocatedMachinesByPeer = this.resourceAllocationManager.getNumberOfAllocatedResourcesByPeer();
		Set<Task> foreignTasks = this.taskManager.getForeignTasks();
		return this.resourceSharingPolicy.getPreemptablePeers(this, consumer, allocatedMachinesByPeer, foreignTasks);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("name", name).append("#machines", machines.size()).toString();
	}

	public Long getAmountOfAvailableTime() {
		long amount = 0;
		for (Entry<String, Long> entry : amountOfAvailableTime.entrySet()) {
			amount += entry.getValue();
		}
		return amount;
	}

	public Long getAmountOfWastedTime() {
		long amount = 0;
		for (Entry<String, Long> entry : amountOfWastedTime.entrySet()) {
			amount += entry.getValue();
		}
		return amount;
	}

	public Long getAmountOfUsefulTime() {
		long amount = 0;
		for (Entry<String, Long> entry : amountOfUsefulTime.entrySet()) {
			amount += entry.getValue();
		}
		return amount;
	}

	public String getMachineName(long id) {
		return getName() + ".m_" + id;
	}

	public Processor getReferenceProcessor() {
		if (referenceMachine == null) {
			long sumOfSpeeds = 0;
			for (Machine machine : machines) {
				sumOfSpeeds += machine.getDefaultProcessor().getSpeed();
			}
			assert sumOfSpeeds > 0 : sumOfSpeeds + " > 0";
			long avgSpeed = Math.round(sumOfSpeeds / (1.0 * machines.size()));
			referenceMachine = new Machine(getName() + "_referenceMachine", avgSpeed);
		}
		return referenceMachine.getDefaultProcessor();
	}

	@Override
	public int compareTo(EventListener o) {
		return this.hashCode() - o.hashCode();
	}

}
