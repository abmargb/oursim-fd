package br.edu.ufcg.lsd.oursim.entities;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.edu.ufcg.lsd.oursim.policy.ranking.ResourceRankingPolicy;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * 
 * This class represents an Job, as usually treated in a bag of task (bot)
 * application. A Job is compound by a collection of independent {@link Task}
 * and its state is actually derived by the state of its internal Tasks.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * @see Task
 * 
 */
public class Job extends ComputableElement implements Comparable<Job> {

	/**
	 * The peer to which this job belongs. The sourcePeer remains unchanged
	 * during its lifetime.
	 */
	private final Peer sourcePeer;

	/**
	 * The collection of Tasks that compose this job.
	 */
	private final List<Task> Tasks;

	private final ResourceRankingPolicy resourceRankingPolicy;

	/**
	 * the level of replication of the Tasks that comprise this job. A <i>value</i>
	 * less than or equal 1 means no replication. A <i>value</i> greater than 1
	 * means that <i>value</i> replies will be created for each task.
	 */
	private int replicationLevel = 0;

	private String userId;

	private long thinkTime;

	private long lastPreemptionTime = -1;

	/**
	 * Field to assure the uniqueness of the id of each task.
	 */
	private static long nextTaskId = 0;

	/**
	 * 
	 * An ordinary constructor for a job. Important: Using this constructor the
	 * Tasks must be added by calling the method {@link Job#addTask(Task)} or
	 * {@link Job#addTask(String, long)} to fulfill the creation of a job.
	 * 
	 * @param id
	 *            The identifier of this Job. The job ID is a positive long. The
	 *            job ID must be unique and remains unchanged during its
	 *            lifetime.
	 * @param submissionTime
	 *            The instant at which this job must be submitted.
	 * @param sourcePeer
	 *            The peer to which this job belongs.
	 */
	public Job(long id, long submissionTime, Peer sourcePeer) {
		super(id, submissionTime);

		assert sourcePeer != null;

		if (sourcePeer == null) {
			throw new InvalidParameterException("The sourcePeer can't be null.");
		}

		this.sourcePeer = sourcePeer;

		sourcePeer.addJob(this);

		this.Tasks = new ArrayList<Task>();

		this.resourceRankingPolicy = new ResourceRankingPolicy(this);

	}

	/**
	 * 
	 * Constructor intended for jobs with only one task. In this special case,
	 * the only task in this job share with it its id.
	 * 
	 * @param id
	 *            The identifier of this Job. The job ID is a positive long. The
	 *            job ID must be unique and remains unchanged during its
	 *            lifetime.
	 * @param submissionTime
	 *            The instant at which this job must be submitted.
	 * @param duration
	 *            The duration in unit of simulation (seconds) of the only task
	 *            contained in this job, considered when executed in an
	 *            reference machine.
	 * @param sourcePeer
	 *            The peer to which this job belongs.
	 */
	public Job(long id, long submissionTime, long duration, Peer sourcePeer) {
		this(id, submissionTime, sourcePeer);

		this.Tasks.add(new Task(this.id, "executable.exe", duration, this.submissionTime, this));

	}

	/**
	 * Adds a task to this job. The task to be added mustn't belong to any other
	 * job.
	 * 
	 * @param Task
	 *            The task to be added.
	 */
	public boolean addTask(Task Task) {
		assert Task.getSourceJob() == null;
		if (Task.getSourceJob() == null) {
			Task.setSourceJob(this);
			this.Tasks.add(Task);
			return true;
		} else {
			return false;
		}
	}

	public int numberOfLocalResourcesUsed() {
		int total = 0;
		for (Task Task : Tasks) {
			total += Task.hasLocallyRunned() ? 1 : 0;
		}
		return total;
	}

	public int numberOfRemoteResourcesUsed() {
		return this.Tasks.size() - numberOfLocalResourcesUsed();

	}

	public double getNSL() {
		return this.getMakeSpan() / (this.sumOfTasksRuntime() * 1.0);
	}

	private long sumOfTasksRuntime() {
		long sum = 0;
		for (Task Task : Tasks) {
			sum += Task.getDuration();
		}
		return sum;
	}

	/**
	 * Adds a task to this job by the information of its executing parameters.
	 * 
	 * @param executable
	 *            The name of the executable of the task.
	 * @param duration
	 *            The duration in unit of simulation (seconds) of the task to be
	 *            added, considered when executed in an reference machine.
	 */
	public void addTask(String executable, long duration) {
		this.Tasks.add(new Task(nextTaskId, executable, duration, submissionTime, this));
		nextTaskId++;
	}

	/**
	 * 
	 * Gets the Tasks that compound this job.
	 * 
	 * @return the Tasks that compound this job.
	 */
	public List<Task> getTasks() {
		return Tasks;
	}

	@Override
	public void finish(long time) {
		// TODO: no esquema atual esse método não tem serventia, pois um job só
		// termina quando todas as suas Tasks tiverem terminado.
		for (Task Task : Tasks) {
			if (!Task.isFinished()) {
				assert false : Task;
				Task.finish(time);
			}
		}
	}

	@Override
	public long getDuration() {
		// a job's duration is a duration of its longest task
		// TODO: another possibility is the sum of all of its Tasks
		long longestTaskDuration = Long.MIN_VALUE;
		for (Task Task : Tasks) {
			longestTaskDuration = Math.max(longestTaskDuration, Task.getDuration());
		}
		return longestTaskDuration;
	}

	@Override
	public Long getStartTime() {
		// a job's start time is the start time of its earlier started task
		long earliestTaskStartTime = Long.MAX_VALUE;
		for (Task Task : Tasks) {
			if (Task.isRunning() || Task.isFinished()) {
				earliestTaskStartTime = Math.min(earliestTaskStartTime, Task.getStartTime());
			}
		}
		return earliestTaskStartTime != Long.MAX_VALUE ? earliestTaskStartTime : null;
	}

	@Override
	public boolean isRunning() {
		// a job is running if at least one of its Tasks is running
		for (Task Task : Tasks) {
			if (Task.isRunning()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Peer getSourcePeer() {
		return sourcePeer;
	}

	/**
	 * 
	 * Gets all the peers that are running Tasks from this job.
	 * 
	 * @return all the peers that are running Tasks from this job.
	 */
	public List<Peer> getTargetPeers() {
		List<Peer> targetPeers = new ArrayList<Peer>();
		for (Task Task : Tasks) {
			targetPeers.add(Task.getTargetPeer());
		}
		return targetPeers;
	}

	@Override
	public void setTargetPeer(Peer targetPeer) {
		for (Task Task : Tasks) {
			Task.setTargetPeer(targetPeer);
		}
	}

	/**
	 * Performs a preemption in all the Tasks that compound this job.
	 * 
	 * @see br.edu.ufcg.lsd.oursim.entities.ComputableElement#preempt(long)
	 */
	@Override
	public void preempt(long time) {
		this.lastPreemptionTime = time;
		// for (Task task : Tasks) {
		// task.preempt(time);
		// }
	}

	@Override
	public void setStartTime(long startTime) {
		for (Task Task : Tasks) {
			Task.setStartTime(startTime);
		}
	}

	@Override
	public Long getEstimatedFinishTime() {
		long lastTaskEstimatedFinishTime = Long.MIN_VALUE;
		boolean allTasksAreRunning = true;
		for (Task Task : Tasks) {
			if (allTasksAreRunning &= Task.isRunning()) {
				lastTaskEstimatedFinishTime = Math.max(lastTaskEstimatedFinishTime, Task.getEstimatedFinishTime());
			} else {
				return null;
			}
		}
		return lastTaskEstimatedFinishTime;
	}

	@Override
	public Long getFinishTime() {
		long lastFinishTime = Long.MIN_VALUE;
		// TODO verificar se isFinished() antes
		for (Task Task : Tasks) {
			if (Task.isFinished() || Task.isAnyReplicaFinished()) {
				lastFinishTime = Math.max(lastFinishTime, Task.getAnyReplicaFinishTime());
			} else {
				return null;
			}
		}
		return lastFinishTime;
	}

	/**
	 * Gets the sum of the preemptions in all Tasks that compound this job.
	 * 
	 * @see br.edu.ufcg.lsd.oursim.entities.ComputableElement#getNumberOfPreemptions()
	 */
	@Override
	public long getNumberOfPreemptions() {
		long totalOfPreemptions = 0;
		for (Task Task : Tasks) {
			totalOfPreemptions += Task.getNumberOfPreemptions();
		}
		return totalOfPreemptions;
	}

	@Override
	public boolean isFinished() {
		return getFinishTime() != null;
	}

	public ResourceRankingPolicy getResourceRequestPolicy() {
		return resourceRankingPolicy;
	}

	/**
	 * Gets the level of replication of the Tasks that comprise this job. A
	 * <i>value</i> less than or equal 1 means no replication. A <i>value</i>
	 * greater than 1 means that <i>value</i> replies will be created for each
	 * task.
	 * 
	 * @return the level of replication of the Tasks that comprise this job.
	 */
	public int getReplicationLevel() {
		return replicationLevel;
	}

	/**
	 * Sets the level of replication of the Tasks that comprise this job. A
	 * <i>value</i> less than or equal 1 means no replication. A <i>value</i>
	 * greater than 1 means that <i>value</i> replies will be created for each
	 * task.
	 * 
	 * @param replicationLevel
	 *            the level of replication of the Tasks that comprise this job
	 */
	public void setReplicationLevel(int replicationLevel) {
		this.replicationLevel = replicationLevel;
	}

	@Override
	public int compareTo(Job j) {
		long diffTime = this.submissionTime - j.getSubmissionTime();
		// o tempo de submission é o mesmo?
		if (diffTime == 0) {
			if (id > j.getId()) {
				return 2;
			} else if (id == j.getId()) {
				assert false;
				return this.hashCode() == j.hashCode() ? 0 : (this.hashCode() > j.hashCode() ? 1 : -1);
			} else {
				return -2;
			}
		} else if (diffTime > 0) { // tempos de submissão diferentes
			// o meu veio depois?
			return 5;
		} else {
			// o meu veio antes
			return -5;
		}
	}

	public double getCost() {
		double totalCost = 0;
		for (Task Task : Tasks) {
			totalCost += Task.getCost();
		}
		return totalCost;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	// public void setSubmissionTime(long submissionTime) {
	// super.setSubmissionTime(submissionTime);
	// for (Task task : Tasks) {
	// task.setSubmissionTime(submissionTime);
	// }
	// }

	public long getThinkTime() {
		return thinkTime;
	}

	public void setThinkTime(long thinkTime) {
		this.thinkTime = thinkTime;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("submissionTime", submissionTime).append("sourcePeer",
				sourcePeer.getName()).append("#Tasks", Tasks.size()).toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Job))
			return false;
		Job castOther = (Job) other;
		return new EqualsBuilder().append(id, castOther.id).append(sourcePeer.getName(), castOther.sourcePeer.getName()).append(replicationLevel,
				castOther.replicationLevel).append(userId, castOther.userId).append(thinkTime, castOther.thinkTime).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(sourcePeer.getName()).append(replicationLevel).append(userId).append(thinkTime).toHashCode();
	}

	public boolean isSingleJob() {
		return Tasks.size() == 1;
	}

	public long getLastPreemptionTime() {
		return lastPreemptionTime;
	}

}
