package br.edu.ufcg.lsd.oursim.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.availability.AvailabilityRecord;

public final class AvailabilityTraceFormat {

	public final static boolean validate() {
		return false;
	}

	public final static void addResourcesToPeer(Peer peer, String gridDescriptionFilePath) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(gridDescriptionFilePath));
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			String machineName = scLine.next();
			int clockRate = scLine.nextInt();
			String next = scLine.next();
			double nextDouble = Double.parseDouble(next);
			long mipsRating = Math.round(nextDouble);
			String machineFullName = machineName;
			peer.addMachine(new Machine(machineFullName, mipsRating));
		}

	}

	public static AvailabilityRecord createAvailabilityRecordFromAvailabilityFormat(String line, long startingTime) {
		Scanner scLine = new Scanner(line);
		String machineName = scLine.next();
		long timestamp = scLine.nextLong() - startingTime;
		long duration = scLine.nextLong();
		assert duration > 0;
		return new AvailabilityRecord(machineName, timestamp, duration);
	}

	public static long extractTimeFromFirstAvailabilityRecord(String availabilityFilePath, boolean hasHeader) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(availabilityFilePath));
		if (hasHeader) {
			sc.nextLine();// TODO desconsidera a primeira linha (cabeçalho)
		}
		String firstLine = sc.nextLine();
		AvailabilityRecord avRecord = createAvailabilityRecordFromAvailabilityFormat(firstLine, 0);
		sc.close();
		return avRecord.getTime();
	}

}
