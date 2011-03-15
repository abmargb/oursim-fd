package br.edu.ufcg.lsd.oursim.io.input.availability;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map.Entry;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.Input;
import br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel.StateModel;
import br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel.StateModelImp;

public class MarkovModelAvailabilityCharacterizationBKP implements Input<AvailabilityRecord> {

	public static final double _00 = 0.0;

	private long startingTime;
	private long quantDeSegundos;

	private Map<Machine, MarkovGenerator<ObservationDiscrete>> machine2AvGenerator;
	private Map<Machine, Long> machine2Time;

	private AvailabilityRecord nextAV;
	private Queue<AvailabilityRecord> nextAvailabilityRecords;
	private boolean shouldStop = false;

	public MarkovModelAvailabilityCharacterizationBKP(Map<String, Peer> peers, long quantDeSegundos, long startingTime) throws FileNotFoundException {
		assert quantDeSegundos > 0;
		assert startingTime >= 0;
		this.startingTime = startingTime;
		this.quantDeSegundos = quantDeSegundos;
		this.nextAvailabilityRecords = new PriorityQueue<AvailabilityRecord>();
		this.machine2AvGenerator = new HashMap<Machine, MarkovGenerator<ObservationDiscrete>>();
		this.machine2Time = new HashMap<Machine, Long>();
		for (Peer peer : peers.values()) {
			for (Machine machine : peer.getMachines()) {
				assert !this.machine2AvGenerator.containsKey(machine);
				this.machine2AvGenerator.put(machine, new MarkovGenerator<ObservationDiscrete>(buildHmm()));
				this.machine2Time.put(machine, this.startingTime);
			}
		}
	}

	private BufferedWriter bw = null;

	public void setBuffer(BufferedWriter utilizationBuffer) {
		this.bw = utilizationBuffer;
		try {
			this.bw.append("time:event:machine:duration\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
	}

	@Override
	public AvailabilityRecord peek() {
		if (!shouldStop) {
			if (this.nextAV == null && !this.nextAvailabilityRecords.isEmpty()) {
				this.nextAV = nextAvailabilityRecords.poll();
			} else if (this.nextAvailabilityRecords.isEmpty()) {
				for (Entry<Machine, MarkovGenerator<ObservationDiscrete>> entry : machine2AvGenerator.entrySet()) {
					Machine machine = entry.getKey();
					MarkovGenerator<ObservationDiscrete> markovGenerator = entry.getValue();
					generateAvailabilityForNextInvocations(machine, markovGenerator);
				}
				this.nextAV = nextAvailabilityRecords.poll();
			}
		}

		if (shouldStop || (this.nextAV != null && this.nextAV.getTime() > this.quantDeSegundos)) {
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
			for (Entry<Machine, MarkovGenerator<ObservationDiscrete>> entry : machine2AvGenerator.entrySet()) {
				Machine machine = entry.getKey();
				if (machine.getName().equals(polledAV.getMachineName())) {
					MarkovGenerator<ObservationDiscrete> markovGenerator = entry.getValue();
					generateAvailabilityForNextInvocations(machine, markovGenerator);
				}
			}
		}

		try {
			if (bw != null) {
				bw.append(polledAV.getTime() + ":AV:" + polledAV.getMachineName() + ":" + polledAV.getDuration()).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return polledAV;
	}

	private void generateAvailabilityForNextInvocations(Machine machine, MarkovGenerator<ObservationDiscrete> markovGenerator) {
		Long avDuration;
		Long naDuration;
		// a duração do período de disponibilidade
		StateModel stateModel = null;
		do {
			ObservationDiscrete observation = markovGenerator.observation();
			stateModel = (StateModel) observation.value;
			avDuration = stateModel.getDuration();
		} while (avDuration == 0 || !stateModel.isAvailability());
		assert stateModel.isAvailability() : stateModel;

		long time = machine2Time.get(machine);
		AvailabilityRecord availabilityRecord = new AvailabilityRecord(machine.getName(), time, avDuration);
		this.nextAvailabilityRecords.add(availabilityRecord);

		// agora a duração do período de INdisponibilidade
		do {
			ObservationDiscrete observation = markovGenerator.observation();
			stateModel = (StateModel) observation.value;
			naDuration = stateModel.getDuration();
		} while (naDuration == 0 || stateModel.isAvailability());
		assert !stateModel.isAvailability() : stateModel;
		this.machine2Time.put(machine, time + avDuration + naDuration);
	}

	public void stop() {
		this.shouldStop = true;
	}

	protected Hmm buildHmm() {

		Hmm<ObservationDiscrete<StateModelImp>> hmm = new Hmm<ObservationDiscrete<StateModelImp>>(StateModelImp.numOfStates(),
				new OpdfDiscreteFactory<StateModelImp>(StateModelImp.class));
		hmm.setPi(StateModelImp.AV_SHORT.getIndex(), 0.39);
		hmm.setPi(StateModelImp.AV_MED.getIndex(), 0.38);
		hmm.setPi(StateModelImp.AV_LONG.getIndex(), 0.23);
		hmm.setPi(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), 0.78);
		hmm.setPi(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), 0.22);
		hmm.setPi(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), 0.78);
		hmm.setPi(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), 0.22);
		hmm.setPi(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), 0.78);
		hmm.setPi(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), 0.22);

		hmm.setOpdf(StateModelImp.AV_SHORT.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { 1.0, _00, _00, _00, _00, _00, _00,
				_00, _00 }));
		hmm.setOpdf(StateModelImp.AV_MED.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, 1.0, _00, _00, _00, _00, _00,
				_00, _00 }));
		hmm.setOpdf(StateModelImp.AV_LONG.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, _00, 1.0, _00, _00, _00, _00,
				_00, _00 }));
		hmm.setOpdf(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, _00, _00, 1.0,
				_00, _00, _00, _00, _00 }));
		hmm.setOpdf(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, _00, _00, _00,
				1.0, _00, _00, _00, _00 }));
		hmm.setOpdf(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, _00, _00, _00, _00,
				1.0, _00, _00, _00 }));
		hmm.setOpdf(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, _00, _00, _00, _00,
				_00, 1.0, _00, _00 }));
		hmm.setOpdf(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, _00, _00, _00,
				_00, _00, _00, 1.0, _00 }));
		hmm.setOpdf(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), new OpdfDiscrete<StateModelImp>(StateModelImp.class, new double[] { _00, _00, _00, _00, _00,
				_00, _00, _00, 1.0 }));

		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), 0.71);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), 0.29);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), 0.76);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), 0.24);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), 0.74);
		hmm.setAij(StateModelImp.AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), 0.26);

		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.AV_SHORT.getIndex(), 0.64);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.AV_MED.getIndex(), 0.25);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.AV_LONG.getIndex(), 0.11);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.AV_SHORT.getIndex(), 0.64);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.AV_MED.getIndex(), 0.25);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.AV_LONG.getIndex(), 0.11);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.AV_SHORT.getIndex(), 0.29);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.AV_MED.getIndex(), 0.52);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.AV_LONG.getIndex(), 0.19);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.AV_SHORT.getIndex(), 0.29);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.AV_MED.getIndex(), 0.52);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.AV_LONG.getIndex(), 0.19);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.AV_SHORT.getIndex(), 0.21);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.AV_MED.getIndex(), 0.31);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.AV_LONG.getIndex(), 0.48);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.AV_SHORT.getIndex(), 0.21);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.AV_MED.getIndex(), 0.31);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.AV_LONG.getIndex(), 0.48);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_SHORT.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_MED.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.NA_SHORT_FROM_AV_LONG.getIndex(), _00);
		hmm.setAij(StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), StateModelImp.NA_LONG_FROM_AV_LONG.getIndex(), _00);

		return hmm;

	}

}
