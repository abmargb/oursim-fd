package br.edu.ufcg.lsd.oursim.io.input.availability;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;

public abstract class SyntheticAvailabilityCharacterizationAbstract implements Input<AvailabilityRecord> {

	protected long startingTime;
	protected long amountOfSeconds;

	protected Map<Machine, Long> machine2Time;
	protected Map<String, Machine> machines;

	protected AvailabilityRecord nextAV;
	protected Queue<AvailabilityRecord> nextAvailabilityRecords;
	protected boolean shouldStop = false;

	protected BufferedWriter bw = null;

	public SyntheticAvailabilityCharacterizationAbstract(Map<String, Peer> peers, long amountOfSeconds, long startingTime) throws FileNotFoundException {
		assert amountOfSeconds > 0;
		assert startingTime >= 0;
		this.startingTime = startingTime;
		this.amountOfSeconds = amountOfSeconds;
		this.nextAvailabilityRecords = new PriorityQueue<AvailabilityRecord>();
		this.machine2Time = new HashMap<Machine, Long>();
		this.machine2Time = new HashMap<Machine, Long>();
		this.machines = new HashMap<String, Machine>();
		for (Peer peer : peers.values()) {
			for (Machine machine : peer.getMachines()) {
				assert !this.machines.containsKey(machine);
				this.machines.put(machine.getName(), machine);
			}
		}
	}

	@Override
	public void close() {
		try {
			this.bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public AvailabilityRecord peek() {
		if (!shouldStop) {
			if (this.nextAV == null && !this.nextAvailabilityRecords.isEmpty()) {
				this.nextAV = nextAvailabilityRecords.poll();
			} else if (this.nextAvailabilityRecords.isEmpty()) {
				for (Machine machine : machine2Time.keySet()) {
					generateAvailabilityForNextInvocations(machine);
				}
				this.nextAV = nextAvailabilityRecords.poll();
			}
		}

		if (shouldStop || (this.nextAV != null && this.nextAV.getTime() > this.amountOfSeconds)) {
			this.nextAvailabilityRecords.clear();
			this.nextAV = null;
			this.shouldStop = true;
		}

		return this.nextAV;
	}

	@Override
	public AvailabilityRecord poll() {
		AvailabilityRecord polledAV = this.peek();
		this.nextAV = null;

		if (polledAV != null) {
			generateAvailabilityForNextInvocations(machines.get(polledAV.getMachineName()));
		}

		printEvent(polledAV);

		return polledAV;
	}

	protected abstract void generateAvailabilityForNextInvocations(Machine machine);

	public void stop() {
		this.shouldStop = true;
		this.nextAvailabilityRecords.clear();
		this.nextAV = null;
	}

	public void setBuffer(BufferedWriter utilizationBuffer) {
		this.bw = utilizationBuffer;
		try {
			this.bw.append("time:event:machine:duration\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printEvent(AvailabilityRecord polledAV) {
		try {
			if (bw != null && polledAV != null) {
				bw.append(polledAV.getTime() + ":AV:" + polledAV.getMachineName() + ":" + polledAV.getDuration()).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}