package br.edu.ufcg.lsd.oursim.io.input.workload;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 30/08/2010
 * 
 */
public class MarcusWorkload2 implements Workload {

	private Map<String, Peer> peers;
	private Scanner scanner;

	private String lastReadLine = null;

	private long quantDeSegundos;

	private Job nextJob;

	public MarcusWorkload2(String workloadFilePath, Map<String, Peer> peers, long quantDeSegundos) throws FileNotFoundException {
		this.peers = peers;
		this.scanner = new Scanner(new BufferedReader(new FileReader(workloadFilePath)));
		this.quantDeSegundos = quantDeSegundos;

		scanner.nextLine();

		readAJob(scanner, peers);

	}

	private String readAJob(Scanner scanner, Map<String, Peer> peers) {

		boolean firstIteration = true;

		Job nextJob;

		if (scanner.hasNextLine()) {
			Job currentJob;
			if (firstIteration && lastReadLine != null) {
				currentJob = createJob(lastReadLine, peers);
				firstIteration = false;
			} else {
				lastReadLine = (lastReadLine == null) ? scanner.nextLine() : lastReadLine;
				currentJob = createJob(lastReadLine, peers);
				firstIteration = false;
			}

			while (scanner.hasNextLine() && (nextJob = createJob(lastReadLine = scanner.nextLine(), peers)).getId() == currentJob.getId()) {
				Task Task = nextJob.getTasks().get(0);
				currentJob.addTask(new Task(Task.getId(), "", Task.getDuration(), currentJob.getSubmissionTime(), null));
			}
			this.nextJob = currentJob;
		}
		return lastReadLine;
	}

	@Override
	public Job peek() {
		// TODO: está sempre com uma falha de 1 job, o final
		if (this.nextJob == null && scanner.hasNextLine()) {
			readAJob(scanner, peers);
		}
		return this.nextJob;
	}

	@Override
	public Job poll() {
		Job polledJob = this.peek();
		this.nextJob = null;
		return polledJob;
	}

	private static Job createJob(String line, Map<String, Peer> peers) {
		// taskId time jobId jobSize runtime user peer
		Scanner scLine = new Scanner(line);
		long taskID = scLine.nextLong();
		long time = scLine.nextLong();
		long jobID = scLine.nextLong();
		long jobSize = scLine.nextLong();
		long runTime = scLine.nextLong();
		String userID = scLine.next();
		String siteID = scLine.next();
		assert peers.containsKey(siteID) : siteID + " -> " + line;
		Job job = new Job(jobID, time, peers.get(siteID));
		job.addTask(new Task(taskID, "", runTime, time, null));
		job.setUserId(userID);
		return job;
	}

	@Override
	public boolean merge(Workload other) {
		return false;
	}

	@Override
	public void close() {
		this.scanner.close();
	}

	@Override
	public void stop() {
		this.scanner.close();
		this.scanner = new Scanner("");
	}

}
