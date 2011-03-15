package br.edu.ufcg.lsd.oursim.dispatchableevents.jobevents;

import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Job;

public class JobEventCounter extends JobEventListenerAdapter {

	private int numberOfFinishedJobs = 0;

	private int numberOfPreemptionsForAllJobs = 0;

	private double totalCostOfAllFinishedJobs = 0;

	private double totalCostOfAllPreemptedJobs = 0;

	private long makespan = 0;

	private Set<Long> idsOfFinishedJobs = new HashSet<Long>();

	private Set<Long> idsOfSubmittedJobs = new HashSet<Long>();

	public long getSumOfJobsMakespan() {
		return makespan;
	}

	@Override
	public final void jobFinished(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();
		this.makespan += job.getMakeSpan();
		this.numberOfFinishedJobs++;
		this.idsOfFinishedJobs.add(job.getId());
		this.totalCostOfAllFinishedJobs += job.getCost();
		assert numberOfFinishedJobs == idsOfFinishedJobs.size() : job + " " + numberOfFinishedJobs + " == " + idsOfFinishedJobs.size();
	}

	@Override
	public final void jobPreempted(Event<Job> jobEvent) {
		Job job = jobEvent.getSource();
		this.numberOfPreemptionsForAllJobs++;
		this.totalCostOfAllPreemptedJobs += job.getCost();
	}

	@Override
	public final void jobSubmitted(Event<Job> jobEvent) {
		this.idsOfSubmittedJobs.add(jobEvent.getSource().getId());
	}

	public final int getNumberOfSubmittedJobs() {
		return idsOfSubmittedJobs.size();
	}

	public final int getNumberOfFinishedJobs() {
		assert numberOfFinishedJobs == idsOfFinishedJobs.size() : numberOfFinishedJobs + " == " + idsOfFinishedJobs.size();
		return numberOfFinishedJobs;
	}

	public final int getNumberOfPreemptionsForAllJobs() {
		return numberOfPreemptionsForAllJobs;
	}

	public double getTotalCostOfAllFinishedJobs() {
		return totalCostOfAllFinishedJobs;
	}

	public double getTotalCostOfAllPreemptedJobs() {
		return totalCostOfAllPreemptedJobs;
	}

}