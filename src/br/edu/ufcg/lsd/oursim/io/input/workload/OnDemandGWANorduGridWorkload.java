package br.edu.ufcg.lsd.oursim.io.input.workload;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
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
public class OnDemandGWANorduGridWorkload implements Workload {

	private Map<String, Peer> peers;
	private long startingTime;
	private Job nextJob;
	private Scanner scanner;

	public OnDemandGWANorduGridWorkload(String workloadFilePath, Map<String, Peer> peers) throws FileNotFoundException {
		this(workloadFilePath, peers, 0);
	}

	public OnDemandGWANorduGridWorkload(String workloadFilePath, Map<String, Peer> peers, long startingTime) throws FileNotFoundException {
		this.startingTime = startingTime;
		this.peers = peers;
		this.scanner = new Scanner(new BufferedReader(new FileReader(workloadFilePath)));
	}

	@Override
	public boolean merge(Workload other) {
		return false;
	}

	@Override
	public void close() {
		this.scanner.close();
	}

	@Override
	public Job peek() {
		if (this.nextJob == null && scanner.hasNextLine()) {
			this.nextJob = GWAFormat.createJobFromGWAFormat(scanner.nextLine(), peers, startingTime);
		}
		return this.nextJob;
	}

	@Override
	public Job poll() {
		Job polledJob = this.peek();
		this.nextJob = null;
		return polledJob;
	}

	@Override
	public void stop() {
		this.nextJob = null;
		this.scanner.close();
		this.scanner = new Scanner("");
	}

}
