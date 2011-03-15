package br.edu.ufcg.lsd.oursim.io.input.workload;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.util.OurSimMainSpikeSolution;

/**
 * 
 * A synthetically generated's workload.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class SyntheticWorkload extends WorkloadAbstract {

	/**
	 * @param runTimeAvg
	 *            the average of the execution of a task.
	 * @param runTimeVar
	 *            the variance of the execution of a task.
	 * @param maxSubmissionInterval
	 *            the maximum interval between the submission of two subsequent
	 *            jobs.
	 * @param numberOfJobs
	 *            the total of jobs.
	 * @param numTasksByJob
	 *            the number of tasks by job.
	 * @param peers
	 *            the peers that the generated jobs wil belong to.
	 */
	public SyntheticWorkload(int runTimeAvg, int runTimeVar, int maxSubmissionInterval, int numberOfJobs, int numTasksByJob, List<Peer> peers) {

		int submissionTime = 0;

		for (int jobId = 0; jobId < numberOfJobs; jobId++) {

			submissionTime += OurSimMainSpikeSolution.RANDOM.nextInt(maxSubmissionInterval);

			double peerIndexD = Math.abs(OurSimMainSpikeSolution.RANDOM.nextGaussian());
			peerIndexD *= peers.size() / 3.0;
			peerIndexD = peerIndexD > peers.size() ? peers.size() - 1 : peerIndexD;

			int peerIndex = (int) (peerIndexD);
			int runTimeDuration = runTimeAvg + OurSimMainSpikeSolution.RANDOM.nextInt(runTimeVar);
			Peer sourcePeer = peers.get(peerIndex);

			Job job = new Job(jobId, submissionTime, sourcePeer);

			for (int i = 0; i < numTasksByJob; i++) {
				job.addTask("", runTimeDuration);
			}

			inputs.add(job);

		}

	}

	/**
	 * Prints out this synthetic workload in a file.
	 * 
	 * @param fileName
	 *            the name of the file in which this workload will be printed
	 *            out.
	 * @throws IOException
	 *             in case of problem with the file.
	 */
	public void save(String fileName) throws IOException {
		PrintStream out = new PrintStream(fileName);
		for (Job job : inputs) {
			out.printf("%s %s %s %s\n", job.getId(), job.getSourcePeer().getName(), job.getDuration(), job.getSubmissionTime());
		}
		out.close();
	}

}
