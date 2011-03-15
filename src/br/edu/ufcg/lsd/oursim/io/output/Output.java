package br.edu.ufcg.lsd.oursim.io.output;

import java.io.Closeable;
import java.io.IOException;

import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.taskevents.TaskEventListener;
import br.edu.ufcg.lsd.oursim.dispatchableevents.workerevents.WorkerEventListener;

/**
 * 
 * The generic (Job based) output of a simulation.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public interface Output extends JobEventListener, TaskEventListener, WorkerEventListener, Closeable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	void close() throws IOException;

}
