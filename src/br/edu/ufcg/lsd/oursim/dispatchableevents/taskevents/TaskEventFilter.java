package br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventFilter;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * The filter that determines which events related to tasks the listener wants
 * to be notified.
 * 
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public interface TaskEventFilter extends EventFilter<Event<Task>> {

	/**
	 * A lenient TaskEventFilter that accepts all events.
	 */
	TaskEventFilter ACCEPT_ALL = new TaskEventFilter() {

		@Override
		public boolean accept(Event<Task> taskEvent) {
			return true;
		}

	};

	@Override
	boolean accept(Event<Task> taskEvent);

}