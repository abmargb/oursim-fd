package br.edu.ufcg.lsd.oursim.io.input.workload;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents.JobEventListenerAdapter;
import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.util.TimeUtil;

/**
 * 
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 30/08/2010
 * 
 */
public class MarcusWorkload extends JobEventListenerAdapter implements Workload {

	private Map<String, Peer> peers;
	private Scanner scanner;

	private PriorityQueue<Job> nextJobs;

	private String lastReadLine = null;

	private Map<String, LinkedList<Job>> user2NextJobs;

	private long quantDeSegundos;

	public MarcusWorkload(String workloadFilePath, Map<String, Peer> peers, long quantDeSegundos) throws FileNotFoundException {
		this.peers = peers;
		this.scanner = new Scanner(new BufferedReader(new FileReader(workloadFilePath)));
		this.user2NextJobs = new HashMap<String, LinkedList<Job>>();
		this.quantDeSegundos = quantDeSegundos;
		this.nextJobs = new PriorityQueue<Job>(100, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				return (int) (o1.getSubmissionTime() - o2.getSubmissionTime());
			}

		});

		scanner.nextLine();

		readATurnOfJobs(scanner, peers, true);

	}

	private String readATurnOfJobs(Scanner scanner, Map<String, Peer> peers, boolean firstTurn) {

		Job nextJob;

		boolean firstIteration = true;

		if (scanner.hasNextLine()) {
			Set<String> usersRead = new HashSet<String>();
			boolean stop = false;
			while (!stop) {
				Job currentJob;
				if (firstIteration && lastReadLine != null) {
					currentJob = createJob(lastReadLine, peers, -1);
					firstIteration = false;
				} else {
					lastReadLine = (lastReadLine == null) ? scanner.nextLine() : lastReadLine;
					currentJob = createJob(lastReadLine, peers, -1);
					firstIteration = false;
				}

				long thinkTimeFromFirstJob = currentJob.getSubmissionTime();
				while (scanner.hasNextLine()
						&& (nextJob = createJob(lastReadLine = scanner.nextLine(), peers, thinkTimeFromFirstJob)).getId() == currentJob.getId()) {
					Task Task = nextJob.getTasks().get(0);
					currentJob.addTask(new Task(Task.getId(), "", Task.getDuration(), currentJob.getSubmissionTime(), null));
				}
				if (!usersRead.contains(currentJob.getUserId())) {
					usersRead.add(currentJob.getUserId());
				} else {
					stop = true;
				}
				if (!scanner.hasNextLine()) {
					stop = true;
				}
				if (!user2NextJobs.containsKey(currentJob.getUserId())) {
					user2NextJobs.put(currentJob.getUserId(), new LinkedList<Job>());
				}
				if (firstTurn) {
					this.nextJobs.add(currentJob);
				} else {
					this.user2NextJobs.get(currentJob.getUserId()).add(currentJob);
				}
			}
		}
		return lastReadLine;
	}

	@Override
	public void jobFinished(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();
		LinkedList<Job> nextJobsFromUser = user2NextJobs.get(job.getUserId());
		if (!nextJobsFromUser.isEmpty()) {
			Job theJob = nextJobsFromUser.removeFirst();
			//XXX precisa descomentar a linha de baixo para o workload parser funcionar
//			theJob.setSubmissionTime(job.getFinishTime() + job.getSubmissionTime());
			// System.out.println(theJob.getSubmissionTime());
			this.nextJobs.add(theJob);
		} else {
			readATurnOfJobs(scanner, peers, false);
			nextJobsFromUser = user2NextJobs.get(job.getUserId());
			if (!nextJobsFromUser.isEmpty()) {
				Job theJob = nextJobsFromUser.removeFirst();
				//XXX precisa descomentar a linha de baixo para o workload parser funcionar
//				theJob.setSubmissionTime(job.getFinishTime() + job.getSubmissionTime());
				// System.out.println(theJob.getSubmissionTime());
				this.nextJobs.add(theJob);
			}
		}
	}

	@Override
	public Job peek() {
		Job peekedJob = nextJobs.peek();
		if (peekedJob.getSubmissionTime() > quantDeSegundos) {
			return null;
		} else {
			return peekedJob;
		}
	}

	@Override
	public Job poll() {
		// for (Entry<String, LinkedList<Job>> entry : user2NextJobs.entrySet())
		// {
		// System.out.println(entry.getKey()+"->"+entry.getValue().size());
		// }
		Job polledJob = nextJobs.poll();
		if (polledJob.getSubmissionTime() > quantDeSegundos) {
			return null;
		} else {
			return polledJob;
		}
	}

	private static Job createJob(String line, Map<String, Peer> peers, long firstJobThinkTime) {
		// taskId, jobId, thinkTime, runtime, userId, siteId
		System.out.println(line);
		Scanner scLine = new Scanner(line);
		long taskID = scLine.nextLong();
		long jobID = scLine.nextLong();
		long thinkTime = scLine.nextLong() * TimeUtil.ONE_MINUTE;
		thinkTime = thinkTime == -1 ? firstJobThinkTime : thinkTime;
		assert thinkTime != -1;
		long runTime = scLine.nextLong();
		String userID = scLine.next();
		String siteID = scLine.next();
		assert peers.containsKey(siteID) : siteID + " -> " + line;		
		Job job = new Job(jobID, thinkTime, peers.get(siteID));
		job.addTask(new Task(taskID, "", runTime, 0, null));
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

	public static void main(String[] args) {
		//3958016418195436
		System.out.println(Long.MAX_VALUE);// 9.223.372.036.854.775.807
		System.out.println(Long.MIN_VALUE);// -9.223.372.036.854.775.808
		// 4.649.040.978.585.894.180
		// -9.148.662.116.537.765.584
	}

}
