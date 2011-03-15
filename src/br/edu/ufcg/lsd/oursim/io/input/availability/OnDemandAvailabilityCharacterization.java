package br.edu.ufcg.lsd.oursim.io.input.availability;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.util.AvailabilityTraceFormat;

public class OnDemandAvailabilityCharacterization implements Input<AvailabilityRecord> {

	private long startingTime;
	private AvailabilityRecord nextAV;
	private Scanner scanner;

	public OnDemandAvailabilityCharacterization(String workloadFilePath) throws FileNotFoundException {
		this(workloadFilePath, 0);
	}

	public OnDemandAvailabilityCharacterization(String workloadFilePath, long startingTime) throws FileNotFoundException {
		this.startingTime = startingTime;
		this.scanner = new Scanner(new BufferedReader(new FileReader(workloadFilePath)));
	}

	@Override
	public void close() {
		this.scanner.close();
	}

	@Override
	public AvailabilityRecord peek() {
		if (this.nextAV == null && scanner.hasNextLine()) {
			this.nextAV = AvailabilityTraceFormat.createAvailabilityRecordFromAvailabilityFormat(scanner.nextLine(), startingTime);
		}
		return this.nextAV;
	}

	@Override
	public AvailabilityRecord poll() {
		AvailabilityRecord polledAV = this.peek();
		this.nextAV = null;
		return polledAV;
	}

	@Override
	public void stop() {
		this.nextAV = null;
		this.scanner.close();
		this.scanner = new Scanner("");
	}

}
