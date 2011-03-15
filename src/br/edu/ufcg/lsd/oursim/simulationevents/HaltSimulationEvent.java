package br.edu.ufcg.lsd.oursim.simulationevents;

public class HaltSimulationEvent extends TimedEvent {

	public static final int PRIORITY = Integer.MIN_VALUE;

	public String message;

	@Deprecated
	public static final int NOW = -1;

	public HaltSimulationEvent(long finishTime) {
		super(finishTime, PRIORITY);
	}

	public HaltSimulationEvent(long finishTime, String message) {
		super(finishTime, PRIORITY);
		this.message = message;
	}

	@Override
	protected final void doAction() {
	}

	@Override
	public String toString() {
		return HaltSimulationEvent.class.getSimpleName() + " : " + time + " : " + message;
	}

}
