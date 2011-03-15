package br.edu.ufcg.lsd.oursim.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * This class represents an Task, as usually treated in a bag of task (bot)
 * application. A task is a unit of computation in a bot application and is
 * intended to be processed in only one {@link Processor}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * @see Job
 * @see Processor
 */
public class Task extends ComputableElement implements Comparable<Task>, Cloneable {

	private static final long SOURCE_TASK_REPLICA_ID = 0l;

	/**
	 * The duration in unit of simulation (seconds) of this Task, considered
	 * when executed in an reference machine.
	 * 
	 * TODO: specify what a reference machine is.
	 * 
	 */
	private final long duration;

	/**
	 * The executable of this task.
	 */
	private File executable;

	/**
	 * The collection of input to a task. ideally, this mustn't be empty.
	 */
	private List<File> inputs;

	/**
	 * The collection of output of a task. ideally, this mustn't be empty.
	 */
	private List<File> outputs;

	/**
	 * The instant at which this task started to running. Through its lifetime a
	 * task may have several start times, but only the latter represents the
	 * definite initial time. If this task is running this field holds a valid
	 * long value, otherwise this field must remains <code>null</code>.
	 */
	private Long startTime = null;

	/**
	 * The instant at which this task has been finished. Through its lifetime a
	 * task have only one finishTime. As long this task is unfinished, its
	 * finishTime must remains <code>null</code>.
	 */
	private Long finishTime = null;

	/**
	 * The {@link Job} that contains this task.
	 */
	private Job sourceJob;

	/**
	 * The {@link Peer} that holds the {@link Machine} in which this task is
	 * running or have been processed, in case it have been finished. If this
	 * task is not running and hasn't been finished yet, this field remains
	 * <code>null</code>.
	 */
	private Peer targetPeer = null;

	/**
	 * The convenient object that is responsible by the execution of this task.
	 * In the same way of the field {@link #targetPeer}, if this task is not
	 * running and hasn't been finished yet, this field remains
	 * <code>null</code>.
	 */
	private TaskExecution taskExecution;

	/**
	 * The total of preemptions suffered by this task.
	 */
	private int numberOfpreemptions;

	/**
	 * all the replicas of this tasks, including itself
	 */
	private Set<Task> replicas = new TreeSet<Task>();

	private long replicaId;

	private boolean cancelled = false;

	private boolean hasLocallyRunned = false;

	private double bidValue;

	private double cost;

	private double wastedTime;

	private boolean wasRecentlyPreempted = false;

	public Task(long id, String executable, long duration, long submissionTime, Job sourceJob) {
		super(id, submissionTime);
		this.executable = new File(executable, -1);
		this.duration = duration;
		this.sourceJob = sourceJob;

		// toda task também é uma réplica de si mesma.
		this.replicas.add(this);
		this.replicaId = SOURCE_TASK_REPLICA_ID;
	}

	/**
	 * Adds an input file to this task.
	 * 
	 * @param name
	 *            The name of the file, actually this could represent an path.
	 * @param size
	 *            Size in bytes of this File.
	 */
	public void addInputFile(String name, long size) {
		this.inputs.add(new File(name, size));
	}

	/**
	 * Adds an output file to this task.
	 * 
	 * @param name
	 *            The name of the file, actually this could represent an path.
	 * @param size
	 *            Size in bytes of this File.
	 */
	public void addOutputFile(String name, long size) {
		this.outputs.add(new File(name, size));
	}

	/**
	 * @return The job that contains this task.
	 */
	public Job getSourceJob() {
		return sourceJob;
	}

	/**
	 * Sets The job that contains this task.
	 * 
	 * @param sourceJob
	 *            The job that contains this task.
	 */
	void setSourceJob(Job sourceJob) {
		this.sourceJob = sourceJob;
	}

	/**
	 * Gets the peer that holds the {@link Machine} in which this task is
	 * running or have been processed, in case it have been finished. If this
	 * task is not running and hasn't been finished yet, this field remains
	 * <code>null</code>.
	 * 
	 * @return Gets the peer that holds the {@link Machine} in which this task
	 *         is running or have been processed, otherwise <code>null</code>
	 *         is returned.
	 */
	public Peer getTargetPeer() {
		return targetPeer;
	}

	/**
	 * Gets the responsible by the execution of this task.
	 * 
	 * @return the responsible by the execution of this task or
	 *         <code>null</code> if this task is not running and hasn't been
	 *         finished yet.
	 */
	public TaskExecution getTaskExecution() {
		return taskExecution;
	}

	/**
	 * Sets the responsible by the execution of this task.
	 * 
	 * @param taskExecution
	 *            the responsible by the execution of this task.
	 */
	public void setTaskExecution(TaskExecution taskExecution) {
		this.taskExecution = taskExecution;
		this.wasRecentlyPreempted = false;
	}

	public void prioritizeResourcesToConsume(List<Machine> resources) {
		this.getSourceJob().getResourceRequestPolicy().rank(resources);
	}

	@Override
	public void setTargetPeer(Peer targetPeer) {
		assert this.targetPeer == null;
		this.targetPeer = targetPeer;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public Long getStartTime() {
		assert startTime != null || finishTime == null;
		return startTime;
	}

	@Override
	public void finish(long time) {
		assert this.finishTime == null || isAnyReplicaFinished();
		assert this.startTime != null;
		this.hasLocallyRunned = this.sourceJob.getSourcePeer().hasMachine(this.taskExecution.getMachine().getName());
		this.taskExecution.finish();
		this.finishTime = time;
	}

	@Override
	public void preempt(long time) {
		assert this.startTime != null;
		if (this.finishTime == null) {
			this.numberOfpreemptions++;
			this.wastedTime += (time - startTime);
			this.startTime = null;
			this.targetPeer = null;
			this.taskExecution.finish();
			// this.taskExecution = null;
			this.wasRecentlyPreempted = true;
		} else {
			System.err.println("Tentou preemptar uma já concluída: " + time + " " + this);
		}
	}

	public void cancel() {
		assert !this.cancelled;
		this.cancelled = true;
		if (this.taskExecution != null) {
			this.taskExecution.finish();
			// this.taskExecution = null;
		}
	}

	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Invoking this method means this task is ready to be executed, that is,
	 * there is already a {@link #taskExecution} seted in this task.
	 * 
	 * @see br.edu.ufcg.lsd.oursim.entities.ComputableElement#setStartTime(long)
	 */
	@Override
	public void setStartTime(long startTime) {
		assert this.taskExecution != null;
		this.startTime = startTime;
	}

	@Override
	public Long getEstimatedFinishTime() {
		assert taskExecution != null : this;
		assert startTime != null : this;
		if (startTime != null) {
			// return this.getStartTime() +
			// taskExecution.getRemainingTimeToFinish();
			return taskExecution.getEstimatedFinishTime();
		} else {
			return null;
		}
	}

	@Override
	public Long getFinishTime() {
		return finishTime;
	}

	@Override
	public long getNumberOfPreemptions() {
		return numberOfpreemptions;
	}

	@Override
	public Peer getSourcePeer() {
		return this.sourceJob.getSourcePeer();
	}

	@Override
	public boolean isRunning() {
		return this.startTime != null && finishTime == null;
	}

	@Override
	public boolean isFinished() {
		return finishTime != null;
	}

	@Override
	public int compareTo(Task t) {
		long diffTime = this.submissionTime - t.getSubmissionTime();
		// os tempos de submissão são iguais?
		if (diffTime == 0) {
			if (id > t.getId()) {
				return 2;
			} else if (id == t.getId()) {
				// assert false : "\n" + this + "\n" + t;
				// return this.hashCode() == t.hashCode() ? 0 : (this.hashCode()
				// > t.hashCode() ? 1 : -1);
				return this.hashCode() == t.hashCode() ? 0 : (this.replicaId > t.getReplicaId() ? 1 : -1);
			} else {
				return -2;
			}
		} else if (diffTime > 0) { // os tempos de submissão são diferentes
			// o meu veio depois?
			return 5;
		} else {
			// o meu veio antes
			return -5;
		}
	}

	public Task clone() {
		// Task theClone = null;
		// try {
		// theClone = (Task) super.clone();
		// } catch (CloneNotSupportedException e) {
		// assert false;
		// e.printStackTrace();
		// return null;
		// }
		// theClone.replicaId = this.replicas.size();
		// this.replicas.add(theClone);
		// theClone.replicas = this.replicas;
		// return theClone;
		return makeReplica();
	}

	public Task makeReplica() {
		Task theClone = null;
		try {
			theClone = (Task) super.clone();
		} catch (CloneNotSupportedException e) {
			assert false;
			e.printStackTrace();
			return null;
		}
		theClone.replicaId = this.replicas.size();
		this.replicas.add(theClone);
		theClone.replicas = this.replicas;
		return theClone;
	}

	public boolean isAnyReplicaFinished() {
		for (Task replica : getReplicas()) {
			if (replica.isFinished()) {
				return true;
			}
		}
		return false;
	}

	public Long getAnyReplicaFinishTime() {
		for (Task replica : replicas) {
			if (replica.isFinished()) {
				return replica.getFinishTime();
			}
		}
		return null;
	}

	public boolean wasRecentlyPreempted() {
		return wasRecentlyPreempted;
	}

	public boolean wasPreempted() {
		return this.numberOfpreemptions > 0;
	}

	public boolean isAllReplicasFailed() {
		boolean hasAllReplicaFailed = true;
		for (Task reply : replicas) {
			hasAllReplicaFailed &= reply.wasPreempted();
		}
		return hasAllReplicaFailed;
	}

	public Set<Task> getReplicas() {
		assert replicas.contains(this);
		Set<Task> onlyTheReplicas = new HashSet<Task>(replicas);
		onlyTheReplicas.remove(this);
		assert !onlyTheReplicas.contains(this);
		return onlyTheReplicas;
	}

	public Set<Task> getActiveReplicas() {
		Set<Task> onlyTheActiveReplicas = new HashSet<Task>();
		for (Task replica : getReplicas()) {
			if (replica.isActive()) {
				onlyTheActiveReplicas.add(replica);
			}
		}
		return onlyTheActiveReplicas;
	}

	private boolean isActive() {
		// TODO: hora de adicionar uma máquina de estados!
		return this.isRunning() || (!this.isFinished() && !this.wasPreempted() && !this.isCancelled());
	}

	public long getReplicaId() {
		return replicaId;
	}

	public void finishSourceTask() {
		assert this.startTime != null;
		assert this.finishTime != null;
		for (Task replica : replicas) {
			if (replica.getReplicaId() == SOURCE_TASK_REPLICA_ID) {
				replica.finishTime = this.finishTime;
				replica.startTime = this.startTime;
				replica.cancelled = false;
				replica.targetPeer = this.targetPeer;
				replica.hasLocallyRunned = this.hasLocallyRunned;
				break;
			}
		}
	}

	@Override
	public String toString() {
		// [id, duration, submissionTime, startTime, finishTime,
		// numberOfpreemptions]
		// return this.id+"";
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("sourceJob", sourceJob.getId()).append("sourcePeer",
				getSourcePeer().getName()).append("duration", duration).append("submissionTime", submissionTime).append("startTime", startTime).append(
				"finishTime", finishTime).append("targetPeer", targetPeer != null ? targetPeer.getName() : "").append("numberOfpreemptions",
				numberOfpreemptions).append("executable", executable).append("replicaId", replicaId).append("cancelled", cancelled).toString();
	}

	public double getBidValue() {
		return bidValue;
	}

	public void setBidValue(double bidValue) {
		this.bidValue = bidValue;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public boolean hasLocallyRunned() {
		return hasLocallyRunned;
	}

	public double getWastedTime() {
		return wastedTime;
	}

}
