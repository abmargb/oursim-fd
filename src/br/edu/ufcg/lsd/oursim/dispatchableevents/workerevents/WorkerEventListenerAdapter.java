package br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListenerAdapter;

/**
 * 
 * A default (empty) implementation of the listener.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 19/05/2010
 * 
 */
public abstract class WorkerEventListenerAdapter extends EventListenerAdapter implements WorkerEventListener {

	@Override
	public void workerAvailable(Event<String> workerEvent) {
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
	}

	@Override
	public void workerUp(Event<String> workerEvent) {
	}

	@Override
	public void workerDown(Event<String> workerEvent) {
	}

	@Override
	public void workerIdle(Event<String> workerEvent) {
	}

	@Override
	public void workerRunning(Event<String> workerEvent) {
	}

}
