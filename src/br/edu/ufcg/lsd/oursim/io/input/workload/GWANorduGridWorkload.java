package br.edu.ufcg.lsd.oursim.io.input.workload;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.entities.Job;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.util.GWAFormat;

/**
 * 
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 10/06/2010
 * 
 */
public class GWANorduGridWorkload extends WorkloadAbstract {

	public GWANorduGridWorkload(String workloadFilePath, Map<String, Peer> peers) throws FileNotFoundException {
		this.inputs = new PriorityQueue<Job>();
		Scanner sc = new Scanner(new File(workloadFilePath));
		while (sc.hasNextLine()) {
			Job theJob = GWAFormat.createJobFromGWAFormat(sc.nextLine(), peers);
			this.inputs.add(theJob);
		}
	}

}
