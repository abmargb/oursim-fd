package br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * The listener interface for receiving task events. The class that is
 * interested in processing a task event either implements this interface (and
 * all the methods it contains) or extends the abstract
 * {@link TaskEventListenerAdapter} class (overriding only the methods of
 * interest). The listener object created from that class is then registered
 * with a {@link TaskEventDispatcher} using the dispatcher's
 * {@link TaskEventDispatcher#addListener(TaskEventListener)} method. When the
 * task's status changes by virtue of being submitted, started, preempted or
 * finished, the relevant method in the listener object is invoked, and the
 * taskEvent is passed to it.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public interface TaskEventListener extends EventListener {

	void taskSubmitted(Event<Task> taskEvent);

	void taskStarted(Event<Task> taskEvent);

	void taskFinished(Event<Task> taskEvent);

	void taskPreempted(Event<Task> taskEvent);

	void taskCancelled(Event<Task> taskEvent);

}
