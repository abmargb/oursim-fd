package br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListenerAdapter;
import br.edu.ufcg.lsd.oursim.entities.Job;

/**
 * 
 * A default (empty) implementation of the listener.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public abstract class JobEventListenerAdapter extends EventListenerAdapter implements JobEventListener {

	public void jobSubmitted(Event<Job> jobEvent) {
	}

	public void jobStarted(Event<Job> jobEvent) {
	}

	public void jobFinished(Event<Job> jobEvent) {
	}

	public void jobPreempted(Event<Job> jobEvent) {
	}

}
