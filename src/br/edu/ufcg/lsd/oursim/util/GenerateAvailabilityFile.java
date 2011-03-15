package br.edu.ufcg.lsd.oursim.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;

import br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel.HostAvailabilityGenerator;
import br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel.HostAvailabilityGeneratorImp;

public class GenerateAvailabilityFile {

	private static void generateAvailabilityFile(int quantMachines, int periodoDeObservacaoEmHoras, String outputFileName) throws java.io.IOException {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));

//			String[] header = "machine_name  time  duration  type  description".split("\\s+");
			String[] header = "machine_name  time  duration".split("\\s+");

			for (String columnName : header) {
				bw.append(columnName).append(HostAvailabilityGenerator.TAB);
			}

			bw.append(HostAvailabilityGenerator.EOL);

			for (int i = 0; i < quantMachines; i++) {
				HostAvailabilityGenerator ma = new HostAvailabilityGeneratorImp("m_" + i, periodoDeObservacaoEmHoras, bw, true);
				ma.generateAvailabilityObservations();
			}

			// FileUtils.writeStringToFile(new File(outputFileName),
			// outputFileName, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void generateMachinesDescription(int quantMachines, String outputFileName) throws java.io.IOException {

		StringBuilder sb = new StringBuilder();

		// String[] header = "//name memory storage bandwidth mem_provisioner
		// bw_rovisioner allocation_policy processor_elements".split("\\s+");
		String[] header = "name clock mips".split("\\s+");

		for (String columnName : header) {
			sb.append(columnName).append(HostAvailabilityGenerator.TAB);
		}

		sb.append(HostAvailabilityGenerator.EOL);

		// String hostPattern = "\"%s\" 2048 1000000 100000000 simple simple
		// space_shared [ 1000 ; ]\n";
		String hostPattern = "\"%s\" 1000 3000 \n";

		for (int i = 0; i < quantMachines; i++) {
			sb.append(String.format(hostPattern, "m_" + i));
		}

		System.out.println(sb);

		FileUtils.writeStringToFile(new File(outputFileName), sb.toString());
	}

	public static void main(String[] args) throws IOException {
		StopWatch c = new StopWatch();
		c.start();

		int quantMachines = 75 * 250;
		int periodoDeObservacaoEmHoras = 26000;
		//resources/trace_mutka_18750-machines_25920-hours.txt
		//demorou 2:20:26.550
		String outputFileName = String.format("resources/trace_mutka_%s-machines_%s-hours_bla.txt", quantMachines, periodoDeObservacaoEmHoras);

//		generateAvailabilityFile(quantMachines, periodoDeObservacaoEmHoras, outputFileName);
		generateAvailabilityFile(1, 5, outputFileName);

		// for (int i = 0; i < 75; i++) {
		// generateMachinesDescription(quantMachines, "peer_" + i +
		// "_machines_description.txt");
		// }

		c.stop();
		System.out.println(c);
	}

}
