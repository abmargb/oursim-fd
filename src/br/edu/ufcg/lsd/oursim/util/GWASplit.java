package br.edu.ufcg.lsd.oursim.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;

public class GWASplit {

	public static void main1(String[] args) throws ParseException, IOException {

		args = "resources/trace_filtrado1.txt 01/01/2006 01/01/2006 resources/nordugrid_ano_2006.txt".split("\\s+");

		int i = 0;
		String workloadFilePath = args[i++];
		String begin = args[i++];
		String end = args[i++];
		String outputFilePath = args[i++];

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		Date beginDate = formatter.parse(begin);
		Date endDate = formatter.parse(end);

		long beginDateTimeMillis = beginDate.getTime() / 1000;
		long endDateTimeMillis = endDate.getTime() / 1000;

		assert beginDateTimeMillis < endDateTimeMillis;
		performSplit(workloadFilePath, outputFilePath, beginDateTimeMillis, endDateTimeMillis);

	}

	private static void performSplit(String workloadFilePath, String outputFilePath, long beginDateTimeMillis, long endDateTimeMillis)
			throws FileNotFoundException, IOException {
		Scanner sc = new Scanner(new File(workloadFilePath));

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

		String line = null;

		while (sc.hasNextLine()) {

			if (!(line = sc.nextLine()).trim().startsWith("#")) {
				GWAJobDescription gwaJob = GWAFormat.createGWAJobDescription(line);
				if (isBetween(gwaJob.SubmitTime, beginDateTimeMillis, endDateTimeMillis)) {
					writer.append(line).append("\n");
				}
			}
		}

		writer.close();
	}

	private static boolean isBetween(long value, long begin, long end) {
		return value >= begin && value < end;
	}

	private static Collection<Job> getBoTsFromUSer(String workloadFilePath, String userId, long delta) throws FileNotFoundException {
		Collection<Job> jobs = new ArrayList<Job>();

		Scanner sc = new Scanner(new File(workloadFilePath));
		String line = null;

		GWAJobDescription lastGwaJob = null;

		Job currentJob = null;

		while (sc.hasNextLine()) {
			GWAJobDescription gwaJob = null;

			// se não for comentário e for job do usuário
			if (!(line = sc.nextLine()).trim().startsWith("#") && (gwaJob = GWAFormat.createGWAJobDescription(line)).UserID.equals(userId)) {

				lastGwaJob = (lastGwaJob == null) ? gwaJob : lastGwaJob;

				if (gwaJob.SubmitTime <= lastGwaJob.SubmitTime + delta) {
					if (currentJob == null) {
						currentJob = new Job(gwaJob.JobID, gwaJob.SubmitTime, Peer.DEFAULT_PEER);
						jobs.add(currentJob);
					}
					currentJob.addTask(new Task(gwaJob.JobID, "", gwaJob.RunTime, gwaJob.SubmitTime, null));
					lastGwaJob = gwaJob;
				} else {
					currentJob = null;
					lastGwaJob = null;
				}
			}
		}

		return jobs;

	}

	private static void getBoTs(String workloadFilePath, String outputFilePath, long delta) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

		// writer.append("SubmitTime TaskId JobId RunTime UserId SiteId \n");

		Scanner sc = new Scanner(new File(workloadFilePath));
		String line = null;

		Long nextJobId = 0l;

		Map<String, Boolean> isNewJobMap = new HashMap<String, Boolean>();
		Map<String, Long> nextJobIdMap = new HashMap<String, Long>();
		Map<String, GWAJobDescription> lastGwaJobMap = new HashMap<String, GWAJobDescription>();
		Map<String, List<String>> jobsInConstructionMap = new HashMap<String, List<String>>();

		while (sc.hasNextLine()) {
			GWAJobDescription gwaJob = null;

			// se não for comentário
			if (!(line = sc.nextLine()).trim().startsWith("#")) {

				gwaJob = GWAFormat.createGWAJobDescription(line);

				if (!isNewJobMap.containsKey(gwaJob.UserID)) {
					isNewJobMap.put(gwaJob.UserID, true);
					nextJobIdMap.put(gwaJob.UserID, nextJobId++);
					lastGwaJobMap.put(gwaJob.UserID, null);
				}

				GWAJobDescription lastGwaJob = isNewJobMap.get(gwaJob.UserID) ? gwaJob : lastGwaJobMap.get(gwaJob.UserID);

				if (gwaJob.SubmitTime <= lastGwaJob.SubmitTime + delta) {
					if (isNewJobMap.get(gwaJob.UserID)) {
						nextJobIdMap.put(gwaJob.UserID, nextJobId++);
						isNewJobMap.put(gwaJob.UserID, false);
						if (!jobsInConstructionMap.containsKey(gwaJob.UserID)) {
							jobsInConstructionMap.put(gwaJob.UserID, new ArrayList<String>());
						}
					}
					gwaJob.SubmitTime = lastGwaJob.SubmitTime;
					lastGwaJobMap.put(gwaJob.UserID, gwaJob);
					if (gwaJob.RunTime <= 3600) {
						jobsInConstructionMap.get(gwaJob.UserID).add(asRecord(nextJobIdMap.get(gwaJob.UserID), gwaJob, lastGwaJob));
					}
				} else {
					lastGwaJobMap.put(gwaJob.UserID, gwaJob);
					isNewJobMap.put(gwaJob.UserID, true);
					for (String taskRecord : jobsInConstructionMap.get(gwaJob.UserID)) {
						writer.append(taskRecord).append("\n");
					}
					jobsInConstructionMap.get(gwaJob.UserID).clear();
					nextJobIdMap.put(gwaJob.UserID, nextJobId++);
					if (gwaJob.RunTime <= 3600) {
						jobsInConstructionMap.get(gwaJob.UserID).add(asRecord(nextJobIdMap.get(gwaJob.UserID), gwaJob, gwaJob));
					}
				}
			}
		}

		for (List<String> jobs : jobsInConstructionMap.values()) {
			for (String taskRecord : jobs) {
				writer.append(taskRecord).append("\n");
			}
		}

		writer.close();

	}

	private static void getBoTsOkMasDificilDeLer(String workloadFilePath, String outputFilePath, long delta) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

		// writer.append("SubmitTime TaskId JobId RunTime UserId SiteId \n");

		Scanner sc = new Scanner(new File(workloadFilePath));
		String line = null;

		Long nextJobId = 0l;

		Map<String, Boolean> isNewJobMap = new HashMap<String, Boolean>();
		Map<String, Long> nextJobIdMap = new HashMap<String, Long>();
		Map<String, GWAJobDescription> lastGwaJobMap = new HashMap<String, GWAJobDescription>();

		while (sc.hasNextLine()) {
			GWAJobDescription gwaJob = null;

			// se não for comentário
			if (!(line = sc.nextLine()).trim().startsWith("#")) {

				gwaJob = GWAFormat.createGWAJobDescription(line);

				if (!isNewJobMap.containsKey(gwaJob.UserID)) {
					isNewJobMap.put(gwaJob.UserID, true);
					nextJobIdMap.put(gwaJob.UserID, nextJobId++);
					lastGwaJobMap.put(gwaJob.UserID, null);
				}

				GWAJobDescription lastGwaJob = isNewJobMap.get(gwaJob.UserID) ? gwaJob : lastGwaJobMap.get(gwaJob.UserID);

				if (gwaJob.SubmitTime <= lastGwaJob.SubmitTime + delta) {
					if (isNewJobMap.get(gwaJob.UserID)) {
						nextJobIdMap.put(gwaJob.UserID, nextJobId++);
						isNewJobMap.put(gwaJob.UserID, false);
					}
					lastGwaJobMap.put(gwaJob.UserID, gwaJob);
				} else {
					lastGwaJobMap.put(gwaJob.UserID, null);
					isNewJobMap.put(gwaJob.UserID, true);
				}
				writer.append(asRecord(nextJobIdMap.get(gwaJob.UserID), gwaJob)).append("\n");
			}
		}

		writer.close();

	}

	private static void getBoTsBugadoNaoDistingueUsuarios(String workloadFilePath, String outputFilePath, long delta) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

		writer.append("SubmitTime TaskId JobId RunTime UserId SiteId \n");

		Scanner sc = new Scanner(new File(workloadFilePath));
		String line = null;

		GWAJobDescription lastGwaJob = null;

		int nextJobId = 0;

		boolean isNewJob = true;

		while (sc.hasNextLine()) {
			GWAJobDescription gwaJob = null;

			// se não for comentário
			if (!(line = sc.nextLine()).trim().startsWith("#")) {

				gwaJob = GWAFormat.createGWAJobDescription(line);
				lastGwaJob = isNewJob ? gwaJob : lastGwaJob;

				if (gwaJob.SubmitTime <= lastGwaJob.SubmitTime + delta) {
					if (isNewJob) {
						nextJobId++;
						isNewJob = false;
					}
					lastGwaJob = gwaJob;
				} else {
					lastGwaJob = null;
					isNewJob = true;
				}
				writer.append(asRecord(nextJobId, gwaJob)).append("\n");
			}
		}

		writer.close();

	}

	private static String asRecord(long jobId, GWAJobDescription task, GWAJobDescription job) {
		StringBuilder sb = new StringBuilder();
		sb.append(job.SubmitTime).append("\t");
		sb.append(jobId).append("\t");
		sb.append(task.JobID).append("\t");
		sb.append(task.RunTime).append("\t");
		sb.append(task.UserID).append("\t");
		sb.append(task.OrigSiteID);
		return sb.toString();
	}

	private static String asRecord(long jobId, GWAJobDescription gwaJob) {
		StringBuilder sb = new StringBuilder();
		sb.append(gwaJob.SubmitTime).append("\t");
		sb.append(gwaJob.JobID).append("\t");
		sb.append(jobId).append("\t");
		sb.append(gwaJob.RunTime).append("\t");
		sb.append(gwaJob.UserID).append("\t");
		sb.append(gwaJob.OrigSiteID);
		return sb.toString();
	}

	public static void main(String[] args) throws ParseException, IOException {

		args = "U1 U121 U144 U200 U204 U205 U207 U208 U21 U213 U283 U293 U301 U302 U303 U304 U305 U306 U307 U308 U309 U310 U312 U313 U314 U315 U36 U42 U7 U72 U9"
				.split("\\s+");

		Collection<Job> bots = new ArrayList<Job>();

//		getBoTs("resources/nordugrid_janeiro_2006.txt", "resources/nordugrid_janeiro_2006_bots_menor_3600.txt", 120);
		// depois sort -k 3,1 resources/nordugrid_janeiro_2006_bots.txt >
		// resources/nordugrid_janeiro_2006_bots_sorted.tx
		// sort -g -k 1 resources/nordugrid_janeiro_2006_bots_menor_3600.txt >
		// resources/nordugrid_janeiro_2006_bots_menor_3600_sorted.txt
		 
		getBoTs("resources/nordugrid_setembro_2005.txt",
		 "resources/nordugrid_setembro_2005_bots.txt", 120);
		// depois sort -k 3,1 resources/nordugrid_setembro_2005_bots.txt >
		// resources/nordugrid_setembro_2005_bots_sorted.tx
		// grep -rwi "F" oursim-og-trace.txt
		for (Job job : bots) {
			System.out.println(job);
		}

	}

}
