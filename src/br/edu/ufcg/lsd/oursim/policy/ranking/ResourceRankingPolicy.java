package br.edu.ufcg.lsd.oursim.policy.ranking;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Machine;


/**
 * 
 * An policy to prioritize the resources that will be consumed.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class ResourceRankingPolicy extends RankingPolicy<Job, Machine> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param job
	 *            the job who are are requesting.
	 */
	public ResourceRankingPolicy(Job job) {
		super(job);
	}

	@Override
	public void rank(List<Machine> machines) {
		// Gets best speed first
		Collections.sort(machines, new Comparator<Machine>() {
			@Override
			public int compare(Machine o1, Machine o2) {
				long diffSpeed = o2.getDefaultProcessor().getSpeed() - o1.getDefaultProcessor().getSpeed();
				if (diffSpeed != 0) {
					return (int) (diffSpeed);
				} else {
					return o1.compareTo(o2);
				}
			}
		});
	}

}
