package br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventDispatcher;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A dispatcher to the task's related events.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 * @see {@link TaskEventListener}
 * @see {@link TaskEventFilter}
 * 
 */
public class TaskEventDispatcher extends EventDispatcher<Task, TaskEventListener, TaskEventFilter> {

	/**
	 * 
	 * An enumeration of all the types of the events that could be dispatched by
	 * this dispatcher. For each type there is an method responsible to dispatch
	 * it. For example, to {@link TYPE_OF_DISPATCHING#submitted} there is
	 * {@link TaskEventDispatcher#dispatchTaskSubmitted(Task)
	 * 
	 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
	 * @since 19/05/2010
	 * 
	 */
	protected enum TYPE_OF_DISPATCHING {
		submitted, started, preempted, finished, cancelled
	};

	private static TaskEventDispatcher instance = null;

	private TaskEventDispatcher() {
		super();
	}

	public static TaskEventDispatcher getInstance() {
		return instance = (instance != null) ? instance : new TaskEventDispatcher();
	}

	@Override
	public void addListener(TaskEventListener listener) {
		if (!this.getListeners().contains(listener)) {
			this.getListeners().add(listener);
			this.getListenerToFilter().put(listener, TaskEventFilter.ACCEPT_ALL);
		} else {
			assert false;
		}
	}

	@Override
	public boolean removeListener(TaskEventListener listener) {
		return this.getListeners().remove(listener);
	}

	/**
	 * @see {@link TaskEventListener#taskSubmitted(Event)
	 * @param Task
	 */
	public void dispatchTaskSubmitted(Task Task) {
		dispatch(TYPE_OF_DISPATCHING.submitted, Task);
	}

	/**
	 * @see {@link TaskEventListener#taskStarted(Event)
	 * @param Task
	 */
	public void dispatchTaskStarted(Task Task) {
		dispatch(TYPE_OF_DISPATCHING.started, Task);
	}

	/**
	 * @see {@link TaskEventListener#taskFinished(Event)
	 * @param Task
	 */
	public void dispatchTaskFinished(Task Task) {
		dispatch(TYPE_OF_DISPATCHING.finished, Task);
	}

	/**
	 * @see {@link TaskEventListener#taskPreempted(Event)
	 * @param Task
	 * @param preemptionTime
	 */
	public void dispatchTaskPreempted(Task Task, long preemptionTime) {
		dispatch(TYPE_OF_DISPATCHING.preempted, Task, preemptionTime);
	}

	public void dispatchTaskCancelled(Task Task, long cancellingTime) {
		dispatch(TYPE_OF_DISPATCHING.cancelled, Task, cancellingTime);
	}

	
	private void dispatch(TYPE_OF_DISPATCHING type, Task Task, long preemptionTime) {
		dispatch(type, new Event<Task>(preemptionTime, Task));
	}

	private void dispatch(TYPE_OF_DISPATCHING type, Task Task) {
		dispatch(type, new Event<Task>(Task));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void dispatch(Enum type, Event<Task> taskEvent) {
		for (TaskEventListener listener : this.getListeners()) {
			// submitted, started, preempted, finished
			if (this.getListenerToFilter().get(listener).accept(taskEvent)) {
				switch ((TYPE_OF_DISPATCHING) type) {
				case submitted:
					listener.taskSubmitted(taskEvent);
					break;
				case started:
					listener.taskStarted(taskEvent);
					break;
				case preempted:
					listener.taskPreempted(taskEvent);
					break;
				case finished:
					listener.taskFinished(taskEvent);
					break;
				case cancelled:
					listener.taskCancelled(taskEvent);
					break;
				default:
					assert false;
				}
			}
		}
	}

}
