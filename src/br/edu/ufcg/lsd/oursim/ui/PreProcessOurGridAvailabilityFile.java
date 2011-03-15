package br.edu.ufcg.lsd.oursim.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PreProcessOurGridAvailabilityFile {

	public static void main(String[] args) throws IOException {

		args = "/home/edigley/local/traces/oursim/marcus/og_availability.csv /home/edigley/local/traces/oursim/r_scripts/availability/og_availability_pos.csv"
				.split("\\s+");

		String inputFile = args[0];
		String outputFile = args[1];

		Scanner sc = new Scanner(new File(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

		Set<String> avTypes = new HashSet<String>(Arrays.asList("DONATED IDLE IN_USE".split("\\s+")));
		Set<String> unTypes = new HashSet<String>(Arrays.asList("CONTACTING OWNER REMOVED".split("\\s+")));

		Map<String, String> peers = new HashMap<String, String>();

		peers.put("lccpeer@xmpp.ourgrid.org", "lsd");
		peers.put("gaita@gaita.gmf.ufcg.edu.br", "gmf");
		peers.put("peer-lsd@xmpp.ourgrid.org", "lcc-1");
		peers.put("peer-lcc2@xmpp.ourgrid.org", "lcc-2");

		String header = sc.nextLine();
		// bw.write(header + ",\"type\"\n");
		bw.write("time,event,peer,machine,duration\n");

		String lastType = "";
		Long lastWorkerId = Long.MIN_VALUE;
		Long lastTime = null;
		StringBuilder sb = null;
		while (sc.hasNextLine()) {
			// "lastModified","timeOfChange","status","worker_id","address","peer_address"
			// 1254272496873,1254272496873,"CONTACTING",1276,"acordeao_1.gmf.ufcg.edu.br@gaita.gmf.ufcg.edu.br","gaita@gaita.gmf.ufcg.edu.br"

			String line = sc.nextLine();
			Scanner scLine = new Scanner(line);
			scLine.useDelimiter(",");
			Long lastModified = scLine.nextLong();
			Long timeOfChange = scLine.nextLong() / 1000;

			String status = scLine.next();
			status = status.substring(1, status.length() - 1);

			Long worker_id = scLine.nextLong();

			String address = scLine.next();
			address = address.substring(1, address.length() - 1);

			String peer_address = scLine.next();
			peer_address = peer_address.substring(1, peer_address.length() - 1);

			assert avTypes.contains(status) || unTypes.contains(status) : status;

			String type = avTypes.contains(status) ? "AV" : "UN";

			if (!worker_id.equals(lastWorkerId) || !type.equals(lastType)) {
				// bw.write(line + ",\"" + type + "\"\n");
				if (type.equals("AV")) {
					assert peers.containsKey(peer_address) : peer_address;
					String peer = peers.get(peer_address);
					String machine = address.substring(0,address.indexOf("."));
					sb = new StringBuilder();
					sb.append(timeOfChange).append(",")
					.append(type).append(",")
					.append(peer).append(",")
					.append(machine).append(",");
				} else if (worker_id.equals(lastWorkerId)) {
					sb.append(timeOfChange - lastTime).append("\n");
					bw.write(sb.toString());
					sb = null;
				}
			}

			lastType = type;
			lastWorkerId = worker_id;
			lastTime = timeOfChange;
		}

		sc.close();
		bw.close();

	}
}
