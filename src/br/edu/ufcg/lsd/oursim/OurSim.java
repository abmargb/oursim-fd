package br.edu.ufcg.lsd.oursim;

import java.util.List;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventDispatcher;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventFilter;
import br.edu.ufcg.lsd.oursim.entities.Grid;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;
import br.edu.ufcg.lsd.oursim.io.input.availability.DedicatedResourcesAvailabilityCharacterization;
import br.edu.ufcg.lsd.oursim.io.input.workload.Workload;
import br.edu.ufcg.lsd.oursim.policy.JobSchedulerPolicy;
import br.edu.ufcg.lsd.oursim.simulationevents.ActiveEntity;
import br.edu.ufcg.lsd.oursim.simulationevents.EventQueue;
import br.edu.ufcg.lsd.oursim.simulationevents.HaltSimulationEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.TimedEvent;

/**
 * 
 * The base class to a simulation. This is intended to be a seamless class, so
 * user interface's facilities should be implemented in client's classes.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 27/05/2010
 * 
 */
public class OurSim {

	private ActiveEntity activeEntity;

	private EventQueue eventQueue;

	/**
	 * the peers that comprise the grid.
	 * 
	 */
	private Grid grid;

	/**
	 * the scheduler of the jobs.
	 */
	private JobSchedulerPolicy jobScheduler;

	/**
	 * the workload to be processed by the resources of the peers.
	 */
	private Workload workload;

	/**
	 * the characterization of the availability of all resources belonging to
	 * the peers.
	 */
	private Input<? extends AvailabilityRecord> availabilityCharacterization;

	/**
	 * An convenient constructor to simulations that deals <b>only</b> with
	 * dedicated resources.
	 * 
	 * @param queue
	 *            The event queue to drive the simulation.
	 * @param peers
	 *            the peers that comprise the grid.
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 * @param workload
	 *            the workload to be processed by the resources of the peers.
	 */
	public OurSim(EventQueue queue, Grid grid, JobSchedulerPolicy jobScheduler, Workload workload) {
		this(queue, grid, jobScheduler, workload, new DedicatedResourcesAvailabilityCharacterization(grid.getListOfPeers()));
	}

	/**
	 * An ordinary constructor to simulations that deals with resources possibly
	 * volatile.
	 * 
	 * @param queue
	 *            The event queue to drive the simulation.
	 * @param peers
	 *            the peers that comprise the grid.
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 * @param workload
	 *            the workload to be processed by the resources of the peers.
	 * @param availabilityCharacterization
	 *            the characterization of the availability of all resources
	 *            belonging to the peers.
	 */
	public OurSim(EventQueue queue, Grid grid, JobSchedulerPolicy jobScheduler, Workload workload,
			Input<? extends AvailabilityRecord> availabilityCharacterization) {
		this.eventQueue = queue;
		this.grid = grid;
		this.jobScheduler = jobScheduler;
		this.workload = workload;
		this.availabilityCharacterization = availabilityCharacterization;
	}

	/**
	 * Starts the simulation.
	 */
	public void start() {
		prepareListeners(grid.getListOfPeers(), jobScheduler);

		// shares the eventQueue with the scheduler
		this.jobScheduler.setEventQueue(this.activeEntity.getEventQueue());

		// setUP the peers to the simulation
		for (Peer peer : grid.getListOfPeers()) {
			// shares the eventQueue with the peers.
			peer.setEventQueue(this.activeEntity.getEventQueue());
		}

		// adds the workload to the scheduler
		// this.jobScheduler.addWorkload(workload);

		run(this.activeEntity.getEventQueue(), jobScheduler, workload, availabilityCharacterization);

		clearListeners(grid.getListOfPeers(), jobScheduler);
	}

	/**
	 * the method that effectively performs the simulation. This method contains
	 * the main loop guiding the whole simulation.
	 * 
	 * @param queue
	 *            The event queue to drive the simulation.
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 * @param availability
	 *            the characterization of the availability of all resources
	 *            belonging to the peers.
	 */
	private void run(EventQueue queue, JobSchedulerPolicy jobScheduler, Workload workload, Input<? extends AvailabilityRecord> availability) {

		do {

			this.addFutureEvents(workload, availability);

			long currentTime = (queue.peek() != null) ? queue.peek().getTime() : -1;

			// dispatch all the events in current time
			while (queue.peek() != null && queue.peek().getTime() == currentTime) {
				TimedEvent nextEventInCurrentTime = queue.poll();

				// TODO Adicionar a estrutura de listeners para o
				// HaltSimulationEvent
				if (nextEventInCurrentTime instanceof HaltSimulationEvent) {
					queue.clear();
					jobScheduler.stop();
					availability.stop();
				} else {
					nextEventInCurrentTime.action();

					//escalona evento de término caso o workload já tenha sido todo processado
					if (workload.peek() == null && jobScheduler.isFinished() && !queue.hasFutureJobEvents()) {
						this.addHaltEvent(this.activeEntity.getCurrentTime() + 1);
					}
				}
			}

			// after the invocation of the actions of all events in current
			// time, the scheduler must be invoked
			jobScheduler.schedule();

			if (queue.peek() != null) {
				grid.accountForUtilization(queue.getCurrentTime(), jobScheduler.getNumberOfRunningTasks(), jobScheduler.getQueueSize());
			}

		} while (queue.peek() != null || workload.peek() != null || availability.peek() != null);

	}

	/**
	 * Adds all the workers'related and job's submission events to the
	 * simulation event queue.
	 * 
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 * @param availability
	 *            the characterization of the availability of all resources
	 *            belonging to the peers.
	 * @see {@link #addFutureWorkerEventsToEventQueue(EventQueue, Input)}
	 * @see {@link #addFutureJobEventsToEventQueue(EventQueue, Workload)}
	 */
	private void addFutureEvents(Workload workload, Input<? extends AvailabilityRecord> availability) {
		this.addFutureWorkerEventsToEventQueue(availability);
		this.addFutureJobEventsToEventQueue(workload);
	}

	/**
	 * Adds all the next worker's related events to the queue, that is, all the
	 * events schedulled to occurs in the next simulation time.
	 * 
	 * @param availability
	 *            the characterization of the availability of all resources
	 *            belonging to the peers.
	 */
	private void addFutureWorkerEventsToEventQueue(Input<? extends AvailabilityRecord> availability) {
		long nextAvRecordTime = (availability.peek() != null) ? availability.peek().getTime() : -1;
		while (availability.peek() != null && availability.peek().getTime() == nextAvRecordTime) {
			AvailabilityRecord av = availability.poll();
			this.activeEntity.addAvailabilityRecordEvent(av.getTime(), av);
		}
	}

	/**
	 * Adds all the next job's submission events to the queue, that is, all the
	 * events schedulled to occurs in the next simulation time.
	 * 
	 * @param jobScheduler
	 *            the scheduler of the jobs.
	 */
	private void addFutureJobEventsToEventQueue(Workload workload) {
		long nextSubmissionTime = (workload.peek() != null) ? workload.peek().getSubmissionTime() : -1;
		while (workload.peek() != null && workload.peek().getSubmissionTime() == nextSubmissionTime) {
			Job job = workload.poll();
			long time = job.getSubmissionTime();
			this.activeEntity.addSubmitJobEvent(time, job);
		}
	}

	/**
	 * Registers all the listeners to the respective dispatchers. This performs
	 * the registering of the listeners obeying the <i>right</i> order: the
	 * peers are firstly added to the WorkerEventDispatcher, only after the
	 * scheduler is added.To remove, call the method
	 * {@link #clearListeners(List, JobSchedulerPolicy)}.
	 * 
	 * @param peers
	 *            the peers to be registered in {@link WorkerEventDispatcher}.
	 * @param sp
	 *            the scheduler to be registered in
	 *            {@linkplain JobEventDispatcher}, {@link TaskEventDispatcher}
	 *            and {@link WorkerEventDispatcher}.
	 * @see {@link #clearListeners(List, JobSchedulerPolicy)}.
	 */
	private static void prepareListeners(List<Peer> peers, JobSchedulerPolicy sp) {
		JobEventDispatcher.getInstance().addListener(sp);
		TaskEventDispatcher.getInstance().addListener(sp);

		// the peers must be the first added to the WorkerEventDispatcher
		for (final Peer peer : peers) {
			WorkerEventDispatcher.getInstance().addListener(peer, new WorkerEventFilter() {

				@Override
				public boolean accept(Event<String> workerEvent) {
					String machineName = (String) workerEvent.getSource();
					return peer.hasMachine(machineName);
				}

			});
		}

		// the scheduler must be added to WorkerEventDispatcher always
		// after of all peers because of the preference at the process
		WorkerEventDispatcher.getInstance().addListener(sp);

	}

	/**
	 * Removes all the listeners added to the respective dispatchers in
	 * {@link #prepareListeners(List, JobSchedulerPolicy)}.
	 * 
	 * @param peers
	 *            the peers to be removed from {@link WorkerEventDispatcher}.
	 * @param sp
	 *            the scheduler to be removed from
	 *            {@linkplain JobEventDispatcher}, {@link TaskEventDispatcher}
	 *            and {@link WorkerEventDispatcher}.
	 * @see {@link #prepareListeners(List, JobSchedulerPolicy)}.
	 */
	private static void clearListeners(List<Peer> peers, JobSchedulerPolicy sp) {

		for (Peer peer : peers) {
			WorkerEventDispatcher.getInstance().removeListener(peer);
		}

		JobEventDispatcher.getInstance().removeListener(sp);
		TaskEventDispatcher.getInstance().removeListener(sp);
		WorkerEventDispatcher.getInstance().removeListener(sp);

	}

	public void setActiveEntity(ActiveEntity activeEntity) {
		this.activeEntity = activeEntity;
		this.activeEntity.setEventQueue(eventQueue);
	}

	public boolean addHaltEvent(long haltTime) {
		if (this.activeEntity != null) {
			this.activeEntity.addHaltEvent(haltTime);
			return true;
		}
		return false;
	}

}
