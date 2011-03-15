package br.edu.ufcg.lsd.oursim.simulationevents;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.FinishJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.jobevents.SubmitJobEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.CancelledTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.FinishTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.PreemptedTaskEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.taskevents.SubmitTaskEvent;

/**
 * 
 * The data structure responsible for deal with the simulation events. To add an
 * event to this queue the parameters of the event must be passed to the
 * apropriate method must be called. There isn't another way to create and event
 * outside of this package.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @author Matheus G. do Rêgo, matheusgr@lsd.ufcg.edu.br
 * @since 14/05/2010
 * 
 */
public final class EventQueue implements Closeable {

	public static boolean LOG = false;
	public static String LOG_FILEPATH = "events_oursim.txt";

	/**
	 * the current simulation's time.
	 */
	private long currentTime = -1;

	/**
	 * The data structure that holds efectivelly the events.
	 */
	private PriorityQueue<TimedEvent> pq;

	public static long totalNumberOfEvents = 0;

	/**
	 * For cache purpose: when a task or job has been preempted its respective
	 * {@link FinishTaskEvent} and {@link FinishJobEvent} must be canceled, and
	 * for this purpose there are these present cache.
	 */
	private Map<Job, FinishJobEvent> job2FinishJobEvent;
	private Map<Task, FinishTaskEvent> task2FinishTaskEvent;

	/**
	 * To trace the events added to this {@link EventQueue}.
	 */
	private BufferedWriter bw;

	private static EventQueue instance = null;

	private long currentLastJobSubmissionTime = -1;

	private EventQueue() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
		task2FinishTaskEvent = new HashMap<Task, FinishTaskEvent>();
		if (LOG) {
			try {
				bw = new BufferedWriter(new FileWriter(LOG_FILEPATH));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static EventQueue getInstance() {
		return instance = (instance != null) ? instance : new EventQueue();
	}

	/**
	 * Cleans all the state of this EventQueue. Its behaviour is something like
	 * a new instantiation of the EventQueue.
	 */
	public void clear() {
		pq = new PriorityQueue<TimedEvent>();
		job2FinishJobEvent = new HashMap<Job, FinishJobEvent>();
		task2FinishTaskEvent = new HashMap<Task, FinishTaskEvent>();
		currentTime = -1;
		totalNumberOfEvents = 0;
	}

	/**
	 * Adds an generic event to this queue.
	 * 
	 * @param event
	 *            the event to be added.
	 */
	public void addEvent(TimedEvent event) {
		assert event.getTime() >= currentTime : event.getTime() + ">=" + currentTime;

		if (event instanceof SubmitJobEvent || event instanceof SubmitTaskEvent) {
			currentLastJobSubmissionTime = Math.max(event.getTime(), currentLastJobSubmissionTime);
		}

		if (event instanceof FinishJobEvent) {
			FinishJobEvent ev = (FinishJobEvent) event;
			this.job2FinishJobEvent.put(ev.source, ev);
		} else if (event instanceof PreemptedTaskEvent) {
			PreemptedTaskEvent ev = (PreemptedTaskEvent) event;
			assert task2FinishTaskEvent.containsKey(ev.source);
			this.task2FinishTaskEvent.remove(ev.source).cancel();
		} else if (event instanceof FinishTaskEvent) {
			FinishTaskEvent ev = (FinishTaskEvent) event;
			if (task2FinishTaskEvent.containsKey(ev.source)) {
				this.task2FinishTaskEvent.remove(ev.source).cancel();
			}
			this.task2FinishTaskEvent.put(ev.source, ev);
		} else if (event instanceof CancelledTaskEvent) {
			CancelledTaskEvent ev = (CancelledTaskEvent) event;
			if (task2FinishTaskEvent.containsKey(ev.source)) {
				this.task2FinishTaskEvent.remove(ev.source).cancel();
			}
		}

		// TODO: definir o que significa a preempção de um job.
		// assert job2FinishJobEvent.containsKey(job);
		// this.job2FinishJobEvent.remove(job).cancel();

		totalNumberOfEvents++;
		// TODO: Verificar a necessidade desse método
		if (LOG) {
			try {
				bw.append(event.toString()).append("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("add["+getCurrentTime()+"]: " +event.getTime() +"
		// : " +event.getType() + ":" + event);
		pq.add(event);
	}

	/**
	 * Remove an event of this queue.
	 * 
	 * @param event
	 *            the event to be removed.
	 */
	public void removeEvent(TimedEvent event) {
		pq.remove(event);
	}

	/**
	 * Retrieves, but does not remove, the head (first element) of this
	 * eventqueue.
	 * 
	 * @return the head of this list, or <tt>null</tt> if this eventqueue is
	 *         empty.
	 */
	public TimedEvent peek() {
		return pq.peek();
	}

	/**
	 * /** Retrieves and removes the head (first element) of this eventqueue.
	 * 
	 * @return the head of this list, or <tt>null</tt> if this eventqueue is
	 *         empty
	 */
	public TimedEvent poll() {
		// checks if the next event is a valid one
		if (pq.peek() != null && !pq.peek().isCancelled()) {
			if (this.currentTime > pq.peek().getTime()) {
				System.err.println("EventQueue: " + this);
				System.err.println("Event:" + pq.peek());
				System.err.println("Offending Event! " + pq.peek() + ". CurrentTime: " + currentTime);
				System.exit(1);
			}
			this.currentTime = pq.peek().getTime();
		}
		// System.out.println(pq.size());
		TimedEvent polledEvent = pq.poll();
		// System.out.println("poll: " +polledEvent.getTime() +" : "
		// +polledEvent.getType() + ":" + polledEvent);

		return polledEvent;
	}

	/**
	 * Gets the current simulation's time.
	 * 
	 * @return the current simulation's time.
	 */
	public long getCurrentTime() {
		return this.currentTime;
	}

	@Override
	public void close() {
		try {
			if (bw != null) {
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "TimeQueue [pq=" + pq.size() + ", time=" + currentTime + "]";
	}

	public boolean hasFutureJobEvents() {
		return this.currentLastJobSubmissionTime >= this.currentTime;
	}

}