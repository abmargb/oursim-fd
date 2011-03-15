package br.edu.ufcg.lsd.oursim.io.input.workload;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;

/**
 * 
 * An convenient Class to deal with generic workloads.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public abstract class WorkloadAbstract extends InputAbstract<Job> implements Workload {

	@Override
	public boolean merge(Workload other) {
		while (other.peek() != null) {
			// TODO: this.inputs.addLast(other.poll());
			this.inputs.add(other.poll());
		}
		return true;
	}
	
	@Override
	public void stop(){
		this.inputs.clear();
	}

}
