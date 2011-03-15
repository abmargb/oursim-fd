package br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListenerAdapter;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A default (empty) implementation of the listener
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public abstract class TaskEventListenerAdapter extends EventListenerAdapter implements TaskEventListener {

	@Override
	public void taskFinished(Event<Task> taskEvent) {
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
	}

}
