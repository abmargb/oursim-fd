package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.IOException;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;
import br.edu.ufcg.lsd.oursim.entities.Task;

/**
 * 
 * A print-out based implementation of an {@link Output}.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 18/05/2010
 * 
 */
public class TaskPrintOutput extends OutputAdapter {

	public TaskPrintOutput() {
		super();
	}

	public TaskPrintOutput(File file) throws IOException {
		super(file);
		super.appendln("type:time:taskId:replicaId");
	}

	@Override
	public void taskSubmitted(Event<Task> taskEvent) {
		Task Task = taskEvent.getSource();
		super.appendln("(U:" + Task.getSubmissionTime() + ":" + Task.getId() + ":" + Task.getReplicaId() + ":NA)");
	}

	@Override
	public void taskStarted(Event<Task> taskEvent) {
		Task Task = taskEvent.getSource();
		String machineName = Task.getTaskExecution().getMachine().getName();
		super.appendln("(S:" + Task.getStartTime() + ":" + Task.getId() + ":" + Task.getReplicaId() + ":" + machineName + ":" + Task.getEstimatedFinishTime()
				+ ")");
	}

	@Override
	public void taskPreempted(Event<Task> taskEvent) {
		Task Task = taskEvent.getSource();
		String machineName = Task.getTaskExecution().getMachine().getName();
		super.appendln("(P:" + taskEvent.getTime() + ":" + Task.getId() + ":" + Task.getReplicaId() + ":" + machineName + ")");
	}

	@Override
	public void taskFinished(Event<Task> taskEvent) {
		Task Task = taskEvent.getSource();
		String machineName = Task.getTaskExecution().getMachine().getName();
		super.appendln("(F:" + Task.getFinishTime() + ":" + Task.getId() + ":" + Task.getReplicaId() + ":" + machineName + ")");
	}

	@Override
	public void taskCancelled(Event<Task> taskEvent) {
		Task Task = taskEvent.getSource();
		String machineName = Task.getTaskExecution().getMachine().getName();
		super.appendln("(C:" + taskEvent.getTime() + ":" + Task.getId() + ":" + Task.getReplicaId() + ":" + machineName + ")");
	}

}