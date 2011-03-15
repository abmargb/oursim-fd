package br.edu.ufcg.lsd.oursim.entities;

/**
 * 
 * An abstraction to an element that could be computed in a grid. Ultimately,
 * this abstraction represents an task, but it represents an uniform interface
 * to treat both a {@link Task} as a {@link Job}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * @see Job
 * @see Task
 */
public abstract class ComputableElement {

	/**
	 * The identifier of this ComputableElement.
	 */
	protected final long id;

	/**
	 * The instant at which this ComputableElement has been submitted.
	 */
	protected final long submissionTime;//TODO deve ser final

	/**
	 * @param id
	 *            The identifier of this ComputableElement.
	 */
	public ComputableElement(long id, long submissionTime) {
		this.id = id;
		this.submissionTime = submissionTime;
	}

	/**
	 * @return Returns the identifier of this ComputableElement.
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Gets the total of unit of simulation (seconds) elapsed since this
	 * ComputableElement has been submitted until it is finished.
	 * 
	 * @return the makespan or <code>null</code> if this ComputableElement
	 *         hasn't been finished yet.
	 */
	public Long getMakeSpan() {
		return this.isFinished() ? getFinishTime() - getSubmissionTime() : null;
	}

	/**
	 * Gets the total number of seconds elapsed since this ComputableElement has
	 * been running.
	 * 
	 * @return the total number of seconds elapsed since this ComputableElement
	 *         has been running or <code>null</code> if this ComputableElement
	 *         hasn't been finished yet.
	 */
	public Long getRunningTime() {
		return this.isFinished() ? getFinishTime() - getStartTime() : null;
	}

	/**
	 * Gets the total number of seconds elapsed since this ComputableElement has
	 * been running until the currentTime.
	 * 
	 * @param currentTime
	 *            The current time.
	 * @return the total number of seconds elapsed since this ComputableElement
	 *         has been running or <code>null</code> if this ComputableElement
	 *         hasn't been finished yet.
	 */
	public Long getRunningTime(long currentTime) {
		assert currentTime >= getStartTime();
		return this.isRunning() ? currentTime - getStartTime() : null;
	}

	/**
	 * Gets the total of unit of simulation (seconds) that this
	 * ComputableElement has elapsed in a Queue, that is, from the submission
	 * instant until the finish, the time in which this ComputableElement was
	 * not running.
	 * 
	 * @return the queuing time or <code>null</code> if this ComputableElement
	 *         hasn't been finished yet.
	 */
	public Long getQueueingTime() {
		return this.isFinished() ? getMakeSpan() - getRunningTime() : null;
	}

	/**
	 * @return The instant at which this ComputableElement has been submitted.
	 */
	public final long getSubmissionTime() {
		return submissionTime;
	}

	/**
	 * @return The duration in unit of simulation (seconds) of this
	 *         ComputableElement, considered when executed in an reference
	 *         machine.
	 */
	public abstract long getDuration();

	/**
	 * @return The instant at which this ComputableElement started running.
	 */
	public abstract Long getStartTime();

	/**
	 * 
	 * Sets the instant at which this ComputableElement started its computation.
	 * 
	 * @param startTime
	 *            The instant at which this ComputableElement started its
	 *            computation.
	 */
	public abstract void setStartTime(long startTime);

	/**
	 * 
	 * Gets an estimate of when this ComputableElement is going to finish.
	 * Notice: the value is just an estimate because the volatility of the
	 * subjacent resources in which this ComputableElement is being executed.
	 * 
	 * @return an estimate of when this ComputableElement is going to finish.
	 */
	public abstract Long getEstimatedFinishTime();

	/**
	 * The instant at which this ComputableElement has been finished.
	 * 
	 * @return The instant at which this ComputableElement has been finished. If
	 *         this ComputableElement hasn't been finished yet,
	 *         <code>null</code> is returned.
	 */
	public abstract Long getFinishTime();

	/**
	 * Gets The peer to which this ComputableElement belongs
	 * 
	 * @return The peer to which this ComputableElement belongs.
	 */
	public abstract Peer getSourcePeer();

	/**
	 * Sets the peer in which this ComputableElement is going to be executed.
	 * 
	 * @param targetPeer
	 *            the peer in which this ComputableElement is going to be
	 *            executed.
	 */
	public abstract void setTargetPeer(Peer targetPeer);

	/**
	 * 
	 * Gets the number of preemptions that have been performed in this
	 * ComputableElement.
	 * 
	 * @return the number of preemptions that have been performed in this
	 *         ComputableElement.
	 */
	public abstract long getNumberOfPreemptions();

	/**
	 * Performs a preemption in this ComputableElement. This means the subjacent
	 * resources in which this Computable Element was being executed, whatever
	 * the reason, became unavailable for this ComputableElement. Unlike
	 * {@link ComputableElement#finish(long)}, invoking this method means the
	 * computation being performed until the invocation must be wasted.
	 * 
	 * @param preemptionTime
	 *            The instant at which the preemption has been occured.
	 * @see {@link ComputableElement#finish(long)}
	 */
	public abstract void preempt(long preemptionTime);

	/**
	 * 
	 * Finishs the execution of this ComputableElement. Unlike
	 * {@link ComputableElement#preempt(long)}, invoking this method means the
	 * computation being performed until the invocation must be utilized.
	 * 
	 * @param finishTime
	 *            The time at which this ComputableElement has been finished.
	 * @see {@link ComputableElement#preempt(long)}
	 */
	public abstract void finish(long finishTime);

	/**
	 * 
	 * Tests if this ComputableElement is running. A ComputableElement is
	 * running if it has been started and has not yet finished or preempted.
	 * 
	 * @return <code>true</false> if this thread is running; <code>false</false> otherwise.
	 */
	public abstract boolean isRunning();

	/**
	 * 
	 * Tests if this ComputableElement is finished. A ComputableElement is
	 * finished if it has been started and has succesfully been finished.
	 * 
	 * @return <code>true</false> if this thread is finished; <code>false</false> otherwise.
	 */
	public abstract boolean isFinished();

}
