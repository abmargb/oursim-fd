package br.edu.ufcg.lsd.oursim.policy.ranking;

import java.util.List;

/**
 * 
 * Represents a way to determine the preferable resources to be consumed.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 * @param <T>
 *            The type of the entity that are requesting.
 * @param <R>
 *            The type of the resource that are been requested.
 */
public abstract class RankingPolicy<T, R> {

	/**
	 * that who are are requesting.
	 */
	private final T requester;

	/**
	 * An ordinary constructor.
	 * 
	 * @param requester
	 *            that who are are requesting.
	 */
	public RankingPolicy(T requester) {
		this.requester = requester;
	}

	/**
	 * Sorts the collection of resources in a way that the preferable to consume
	 * are firstly accessed.
	 * 
	 * @param resources
	 *            the resources available to be consumed.
	 */
	public abstract void rank(List<R> resources);

	public T getRequester() {
		return requester;
	}

}
