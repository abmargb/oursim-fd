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
 * @since 10/06/2010
 * 
 */
public class OnDemandBoTGWANorduGridWorkload implements Workload {

	private Map<String, Peer> peers;
	private long startingTime;
	private Job nextJob;
	private Scanner scanner;

	public OnDemandBoTGWANorduGridWorkload(String workloadFilePath, Map<String, Peer> peers) throws FileNotFoundException {
		this(workloadFilePath, peers, 0);
	}

	public OnDemandBoTGWANorduGridWorkload(String workloadFilePath, Map<String, Peer> peers, long startingTime) throws FileNotFoundException {
		this.startingTime = startingTime;
		this.peers = peers;
		this.scanner = new Scanner(new BufferedReader(new FileReader(workloadFilePath)));
	}

	@Override
	public boolean merge(Workload other) {
		return false;
	}

	@Override
	public void close() {
		this.scanner.close();
	}

	private Job nextNextJob = null;

	@Override
	public Job peek() {
		if (this.nextJob == null && scanner.hasNextLine()) {
			Job firstJob = (nextNextJob == null) ? createJob(scanner.nextLine(), peers, startingTime) : nextNextJob;
			while (scanner.hasNextLine() && (nextNextJob = createJob(scanner.nextLine(), peers, startingTime)).getId() == firstJob.getId()) {
				Task Task = nextNextJob.getTasks().get(0);
				firstJob.addTask(new Task(Task.getId(), "", Task.getDuration(), firstJob.getSubmissionTime(), null));
//				firstJob.addTask(new Task(task.getId(), "", task.getDuration(), task.getSubmissionTime(), null));
			}
			this.nextJob = firstJob;

		}
		return this.nextJob;
	}

	@Override
	public Job poll() {
		Job polledJob = this.peek();
		this.nextJob = null;
		return polledJob;
	}

	@Override
	public void stop() {
		this.nextJob = null;
		this.scanner.close();
		this.scanner = new Scanner("");
	}

	public final static Job createJob(String line, Map<String, Peer> peers, long startingTime) {
		Scanner scLine = new Scanner(line);
		long submitTime = scLine.nextLong();
		long jobID = scLine.nextLong();
		long taskID = scLine.nextLong();
		long runTime = scLine.nextLong();
		String userID = scLine.next();
		String siteID = scLine.next();
		assert peers.containsKey(siteID) : siteID;
//		Job job = new Job(jobID, submitTime - startingTime, peers.get(siteID));
		Job job = new Job(jobID, submitTime - startingTime, peers.get(siteID));
		job.addTask(new Task(taskID, "", runTime, submitTime - startingTime, null));
		return job;
	}

}
