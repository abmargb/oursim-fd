package br.edu.ufcg.lsd.oursim.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PreProcessOurGridAvailabilityFile1 {

	public static void main(String[] args) throws IOException {

		args = "/home/edigley/local/traces/oursim/r_scripts/availability/av570.txt /home/edigley/local/traces/oursim/r_scripts/availability/av570_pos.txt".split("\\s+");
		
		String inputFile = args[0];
		String outputFile = args[1];

		Scanner sc = new Scanner(new File(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

		String lastType = "";
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			Scanner scLinha = new Scanner(line);
			String time = scLinha.next();
			String machine = scLinha.next();
			String type = scLinha.next();
			if (!type.equals(lastType)) {
				bw.write(line + "\n");
			}
			lastType = type;
		}

		sc.close();
		bw.close();

	}

}
