package br.edu.ufcg.lsd.oursim.io.input.workload;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.io.input.Input;

/**
 * 
 * An collection of jobs intended to be used to generated events of job's
 * submissions. The jobs must be returned in a timer-ordered base, that is, in
 * two subsequent invocations of {@link Workload#poll()}, the
 * {@link Job#getSubmissionTime()} of the second returned <b>must</b> be
 * greater than the first one.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public interface Workload extends Input<Job> {

	/**
	 * Performs a merge of this workload with another one. The other workload
	 * will be invalidated after the calling of this operation.
	 * 
	 * @param other
	 *            the workload with which this is going to be merged.
	 * @return <code>true</code> if the merge was successfully performed,
	 *         <code>false</code> otherwise.
	 */
	boolean merge(Workload other);

}
