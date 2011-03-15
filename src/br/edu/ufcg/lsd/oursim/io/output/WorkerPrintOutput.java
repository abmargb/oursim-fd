package br.edu.ufcg.lsd.oursim.io.output;

import java.io.File;
import java.io.IOException;

import br.edu.ufcg.lsd.oursim.dispatchableevents.Event;

public class WorkerPrintOutput extends OutputAdapter {

	public WorkerPrintOutput() {
		super();
	}

	public WorkerPrintOutput(File file) throws IOException {
		super(file);
		super.appendln("time:event:machine");
	}

	@Override
	public void workerAvailable(Event<String> workerEvent) {
		super.appendln(workerEvent.getTime() + ":AV:" + workerEvent.getSource());
	}

	@Override
	public void workerUnavailable(Event<String> workerEvent) {
		super.appendln(workerEvent.getTime() + ":NA:" + workerEvent.getSource());
	}

}
