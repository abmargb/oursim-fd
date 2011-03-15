package br.edu.ufcg.lsd.oursim.policy.ranking;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.entities.Task;


/**
 * 
 * A policy to prioritize the tasks that can be preempted.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class TaskPreemptionRankingPolicy extends RankingPolicy<Peer, Task> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param peer
	 *            the peer who are are requesting.
	 */
	public TaskPreemptionRankingPolicy(Peer peer) {
		super(peer);
	}

	@Override
	public void rank(List<Task> Tasks) {
		// get recently started task first
		Collections.sort(Tasks, new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
//				return (int) (t2.getStartTime() - t1.getStartTime());
				long diffTime = t2.getStartTime() - t1.getStartTime();
				if (diffTime != 0) {
					return (int) (diffTime);
				} else {
					return t1.compareTo(t2);
				}
			}
		});

	}

}
