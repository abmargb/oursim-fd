package br.edu.ufcg.lsd.oursim.entities;

public class TaskExecution {

	/**
	 * The size in Millions of Instructions (MI) of this Task to be executed in
	 */
	private long size;

	private long previousTime;

	private long remainingSize;

	private Task Task;

	private Processor processor;

	public TaskExecution(Task Task, Processor processor, long startTime) {
		assert !processor.isBusy() : Task + " -> " + processor;
		if (processor.isBusy()) {
			throw new IllegalArgumentException("The processor has been already in execution.");
		}
		this.Task = Task;
		this.processor = processor;
		this.processor.busy();
		// this.size =
		// Processor.EC2_COMPUTE_UNIT.calculateNumberOfInstructionsProcessed(this.task.getDuration());
		this.size = convertTaskDurationToNumberOfInstruction(Task);
		this.remainingSize = size;
		this.previousTime = startTime;
		// TODO talvez nesse momento já devesse setar o startTime da Task, para
		// não deixar para alguém externo fazer isso
	}

	private long convertTaskDurationToNumberOfInstruction(Task Task) {
		Peer sourcePeer = Task.getSourcePeer();
		Processor referenceProcessor = sourcePeer.getReferenceProcessor();
		return referenceProcessor.calculateNumberOfInstructionsProcessed(this.Task.getDuration());
	}

	/**
	 * @param processor
	 * @param currentTime
	 * @return The time lacking to this Task be finished in that processor.
	 */
	public Long updateProcessing(long currentTime) {
		assert currentTime > previousTime;

		// time since last update
		long timeElapsed = currentTime - previousTime;

		// TODO: verificar as consequências do remaining time negativo.
		this.remainingSize -= processor.calculateNumberOfInstructionsProcessed(timeElapsed);

		this.previousTime = currentTime;

		return (remainingSize <= 0) ? 0 : processor.calculateTimeToExecute(remainingSize);

	}

	public Long getRemainingTimeToFinish() {
		if (remainingSize > 0) {
			return processor.calculateTimeToExecute(remainingSize);
		} else {
			return 0L;
		}
	}

	public Long getEstimatedFinishTime() {
		return previousTime + getRemainingTimeToFinish();
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Machine getMachine() {
		return this.processor.getMachine();
	}

	public void finish() {
		this.processor.free();
	}

	public Processor getProcessor() {
		return this.processor;
	}

}
