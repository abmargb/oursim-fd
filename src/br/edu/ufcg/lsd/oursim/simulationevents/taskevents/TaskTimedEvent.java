package br.edu.ufcg.lsd.oursim.simulationevents.taskevents;

import br.edu.ufcg.lsd.oursim.entities.Task;
import br.edu.ufcg.lsd.oursim.simulationevents.TimedEvent;
import br.edu.ufcg.lsd.oursim.simulationevents.TimedEventAbstract;

/**
 * 
 * The root class to all task's related events.
 * 
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 20/05/2010
 * 
 */
public abstract class TaskTimedEvent extends TimedEventAbstract<Task> {

	/**
	 * An ordinary constructor.
	 * 
	 * @param time
	 *            the time at which the event have occurred.
	 * @param priority
	 *            the priority of the event.
	 * @param Task
	 *            the task this event relates to.
	 */
	public TaskTimedEvent(long time, int priority, Task Task) {
		super(time, priority, Task);
	}

	@Override
	public int compareTo(TimedEvent ev) {
		// TODO: Política
		int compareToFromSuper = super.compareTo(ev);
		// o super não foi conclusivo e o outro evento é do mesmo tipo deste?
		if (compareToFromSuper == 0 && ev instanceof TaskTimedEvent) {
			TaskTimedEvent o = (TaskTimedEvent) ev;
			// essa task já foi preemptada?
			if (this.source.getNumberOfPreemptions() > 0) {
				// TODO: definir qual é a política nesse caso
				// a outra também já foi preemptada?
				if (o.source.getNumberOfPreemptions() > 0) {
					// os numeros de preempcoes são diferentes?
					if (o.source.getNumberOfPreemptions() != this.source.getNumberOfPreemptions()) {
						// prioriza a que já foi preemptadas mais vezes
						return (int) (o.source.getNumberOfPreemptions() - this.source.getNumberOfPreemptions());
					} else {
						// se eh tudo igual, então desempata pelo id.
						return (int) (this.source.getId() - o.source.getId());
					}
				} else {
					return -1;
				}
			} else if (o.source.getNumberOfPreemptions() > 0) {
				// se esta não foi, a outra já foi?
				return 1;
			} else {
				// se eh tudo igual, então desempata pelo id.
				return (int) (this.source.getId() - o.source.getId());
			}
		} else {
			return compareToFromSuper;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String type = this.getType();
		String time = Long.toString(this.getTime());
		String peer = this.source.getSourcePeer().getName();
		String taskId = Long.toString(this.source.getId());
		String jobId = Long.toString(this.source.getSourceJob().getId());
		String makespan = this.source.getMakeSpan() + "";
		String runningTime = this.source.getRunningTime() + "";
		String queuingTime = this.source.getQueueingTime() + "";
		sb.append(type).append(" ").append(time).append(" ").append(taskId).append(" ").append(jobId).append(" ").append(peer).append(" ").append(makespan)
				.append(" ").append(runningTime).append(" ").append(queuingTime);
		return sb.toString();
	}

}
