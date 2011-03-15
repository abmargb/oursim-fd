package br.edu.ufcg.lsd.oursim.io.input.availability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.io.input.InputAbstract;
import br.edu.ufcg.lsd.oursim.util.AvailabilityTraceFormat;

public class AvailabilityCharacterization extends InputAbstract<AvailabilityRecord> {

	private long startingTime;
	private boolean hasHeader;

	public AvailabilityCharacterization(String availabilityFilePath) throws FileNotFoundException {
		this(availabilityFilePath, 0, false);
	}

	public AvailabilityCharacterization(String availabilityFilePath, long startingTime, boolean hasHeader) throws FileNotFoundException {
		assert startingTime >= 0;
		this.inputs = new PriorityQueue<AvailabilityRecord>();
		this.startingTime = startingTime;
		this.hasHeader = hasHeader;
		Scanner sc = new Scanner(new File(availabilityFilePath));
		if (this.hasHeader) {
			sc.nextLine();// TODO desconsidera a primeira linha (cabeçalho)
		}
		long previousTime = -1;
		while (sc.hasNextLine()) {
			AvailabilityRecord avRecord = AvailabilityTraceFormat.createAvailabilityRecordFromAvailabilityFormat(sc.nextLine(), this.startingTime);
			assert avRecord.getTime() >= previousTime;
			previousTime = avRecord.getTime();
			this.inputs.add(avRecord);
		}
	}

}
