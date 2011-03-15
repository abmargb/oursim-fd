package br.edu.ufcg.lsd.oursim.simulationevents;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;

/**
 * 
 * This is a convenient interface to state all the entity that might alter the
 * eventQueue or want to know what is the current time of the simulation. This
 * interface should be used carefully and only by entities that really need it.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 01/06/2010
 * 
 */
public interface ActiveEntity {

//XXX	void addNewSpotPriceEvent(SpotPrice spotPrice);
//	
//	void addFullHourCompletedEvent(BidValue bidValue);
	
	/**
	 * Set in this entity the event queue. All the entitys must share the same
	 * instance of the eventqueue to maintain consistency.
	 * 
	 * @param eventQueue
	 *            the eventqueue to be shared.
	 */
	void setEventQueue(EventQueue eventQueue);

	/**
	 * @return the active eventqueue.
	 */
	EventQueue getEventQueue();

	long getCurrentTime();

	/**
	 * Adds an event indicating that a job was submitted.
	 * 
	 * @param submitTime
	 *            the time at which the job has been submitted.
	 * @param job
	 *            the job that has been submitted.
	 */
	void addSubmitJobEvent(long submitTime, Job job);

	/**
	 * Adds an event indicating that a job has been started.
	 * 
	 * @param job
	 *            the job that has been started.
	 */
	void addStartedJobEvent(Job job);

	/**
	 * Adds an event indicating that a job has been preempted.
	 * 
	 * @param preemptionTime
	 *            the time at which the job has been preempted.
	 * @param job
	 *            the job that has been preempted.
	 */
	void addPreemptedJobEvent(long preemptionTime, Job job);

	/**
	 * Adds an event indicating that a job has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the job has been finished.
	 * @param job
	 *            the job that has been finished.
	 */
	void addFinishJobEvent(long finishTime, Job job);

	/**
	 * Adds an event indicating that a task was submitted.
	 * 
	 * @param submitTime
	 *            the time at which the job has been submitted.
	 * @param Task
	 *            the task that has been submitted.
	 */
	void addSubmitTaskEvent(long submitTime, Task Task);

	/**
	 * Adds an event indicating that a task has been started.
	 * 
	 * @param Task
	 *            the task that has been started.
	 */
	void addStartedTaskEvent(Task Task);

	/**
	 * Adds an event indicating that a task has been preempted.
	 * 
	 * @param preemptionTime
	 *            the time at which the task has been preempted.
	 * @param Task
	 *            the task that has been preempted.
	 */
	void addPreemptedTaskEvent(long preemptionTime, Task Task);
	
	void addPreemptedTaskEvent(Task Task);

	/**
	 * Adds an event indicating that a task has been finished.
	 * 
	 * @param finishTime
	 *            the time at which the task has been finished.
	 * @param Task
	 *            the task that has been finished.
	 */
	void addFinishTaskEvent(long finishTime, Task Task);

	/**
	 * Adds an event indicating that a worker has become available. It's
	 * automatically added a future event indicating that the worker has become
	 * unavailable after the duration of the availability period.
	 * 
	 * @param time
	 *            the time at which the machine has become available.
	 * @param machineName
	 *            the name of the machine that has become available.
	 * @param duration
	 *            the duration of the availability period.
	 */
	void addWorkerAvailableEvent(long time, String machineName, long duration);

	void addCancelledTaskEvent(Task Task);

	void addCancelledTaskEvent(long preemptionTime, Task Task);

	void addAvailabilityRecordEvent(long time, AvailabilityRecord avRecord);

	void addHaltEvent(long haltTime);

}