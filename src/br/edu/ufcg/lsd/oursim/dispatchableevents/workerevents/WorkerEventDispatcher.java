package br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventDispatcher;

/**
 * 
 * A dispatcher to the worker's related events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 * @see {@link SpotPriceEventListener}
 * @see {@link SpotPriceEventFilter}
 * 
 */
public class WorkerEventDispatcher extends EventDispatcher<String, WorkerEventListener, WorkerEventFilter> {

	/**
	 * 
	 * An enumeration of all the types of the events that could be dispatched by
	 * this dispatcher. For each type there is an method responsible to dispatch
	 * it. For example, to {@link TYPE_OF_DISPATCHING#available} there is
	 * {@link WorkerEventDispatcher#dispatchWorkerAvailable(String, long)}
	 * 
	 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
	 * @since 19/05/2010
	 * 
	 */
	protected enum TYPE_OF_DISPATCHING {
		up, down, available, unavailable, idle, running
	};

	private static WorkerEventDispatcher instance = null;

	private WorkerEventDispatcher() {
		super();
	}

	public static WorkerEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new WorkerEventDispatcher();
	}

	@Override
	public void addListener(WorkerEventListener listener) {
		if (!this.getListeners().contains(listener)) {
			this.getListeners().add(listener);
			this.getListenerToFilter().put(listener, WorkerEventFilter.ACCEPT_ALL);
		} else {
			assert false;
		}
		assert this.getListenerToFilter().get(listener) != null;
	}

	@Override
	public boolean removeListener(WorkerEventListener listener) {
		return this.getListeners().remove(listener);
	}

	/**
	 * @see {@link SpotPriceEventListener#workerUp(Event)
	 * @param machineName
	 * @param time
	 */
	public void dispatchWorkerUp(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.up, machineName, time);
	}

	/**
	 * @see {@link SpotPriceEventListener#workerDown(Event)
	 * @param machineName
	 * @param time
	 */
	public void dispatchWorkerDown(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.down, machineName, time);
	}

	/**
	 * @see {@link SpotPriceEventListener#workerAvailable(Event)
	 * @param machineName
	 * @param time
	 */
	public void dispatchWorkerAvailable(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.available, machineName, time);
	}

	/**
	 * @see {@link SpotPriceEventListener#workerUnavailable(Event)
	 * @param machineName
	 * @param time
	 */
	public void dispatchWorkerUnavailable(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.unavailable, machineName, time);
	}

	/**
	 * @see {@link SpotPriceEventListener#workerIdle(Event)
	 * @param machineName
	 * @param time
	 */
	public void dispatchWorkerIdle(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.idle, machineName, time);
	}

	/**
	 * @see {@link SpotPriceEventListener#workerRunning(Event)
	 * @param machineName
	 * @param time
	 */
	public void dispatchWorkerRunning(String machineName, long time) {
		dispatch(TYPE_OF_DISPATCHING.running, machineName, time);
	}

	private void dispatch(TYPE_OF_DISPATCHING type, String machineName, long time) {
		dispatch(type, new Event<String>(time, machineName));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void dispatch(Enum type, Event<String> workerEvent) {
		for (WorkerEventListener listener : this.getListeners()) {
			// up, down, available, unavailable, idle, running
			if (this.getListenerToFilter().get(listener).accept(workerEvent)) {
				switch ((TYPE_OF_DISPATCHING) type) {
				case up:
					listener.workerUp(workerEvent);
					break;
				case down:
					listener.workerDown(workerEvent);
					break;
				case available:
					listener.workerAvailable(workerEvent);
					break;
				case unavailable:
					listener.workerUnavailable(workerEvent);
					break;
				case idle:
					listener.workerIdle(workerEvent);
					break;
				case running:
					listener.workerRunning(workerEvent);
					break;
				default:
					assert false;
				}
			}
			//XXX: se ninguem aceitar o evento, então há problema!
		}
	}
}
