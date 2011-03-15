package br.edu.ufcg.lsd.oursim.io.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.EventListener;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A default (empty) implementation of {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public abstract class OutputAdapter implements Output {

	/**
	 * the stream where the results will be printed out.
	 */
	protected BufferedWriter bw = null;

	protected PrintStream out;

	/**
	 * An default constructor. Using this constructor the results will be
	 * printed out in the default output.
	 */
	public OutputAdapter() {
		this.out = System.out;
	}

	/**
	 * Using this constructor the results will be printed out in the file
	 * <code>file</code>.
	 * 
	 * @param file
	 *            The file where the results will be printed out.
	 */
	public OutputAdapter(File file) throws IOException {
		this.bw = new BufferedWriter(new FileWriter(file));
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
	}

	@Override
	public void jobPreempted(Event<Job> jobEvent) {
	}

	@Override
	public void jobStarted(Event<Job> jobEvent) {
	}

	@Override
	public void jobSubmitted(Event<Job> jobEvent) {
	}

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

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
	}

	@Override
	public void workerAvailable(Event<String> workerEvent) {
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

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
	}

	@Override
	public void workerUp(Event<String> workerEvent) {
	}

	@Override
	public void close() throws IOException {
		if (bw != null) {
			this.bw.close();
		}
	}

	protected void appendln(String line) {
		if (bw != null) {
			try {
				this.bw.append(line).append("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			this.out.println(line);
		}
	}

	@Override
	public int compareTo(EventListener o) {
		return this.hashCode() - o.hashCode();
	}
}
