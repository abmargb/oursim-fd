package br.edu.ufcg.lsd.oursim.io.input.availability;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;
import br.edu.ufcg.lsd.oursim.entities.Machine;
import br.edu.ufcg.lsd.oursim.entities.Peer;
import br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel.StateModel;
import br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel.StateModelImp;

public class MarkovModelAvailabilityCharacterization extends SyntheticAvailabilityCharacterizationAbstract {

	public static final double _00 = 0.0;

	private Map<Machine, MarkovGenerator<ObservationDiscrete>> machine2AvGenerator;

	public MarkovModelAvailabilityCharacterization(Map<String, Peer> peers, long amountOfSeconds, long startingTime) throws FileNotFoundException {
		super(peers, amountOfSeconds, startingTime);
		this.machine2AvGenerator = new HashMap<Machine, MarkovGenerator<ObservationDiscrete>>();
		for (Machine machine : machines.values()) {
			assert !this.machine2AvGenerator.containsKey(machine);
			this.machine2AvGenerator.put(machine, new MarkovGenerator<ObservationDiscrete>(buildHmm()));
			this.machine2Time.put(machine, this.startingTime);
		}
	}

	@Override
	protected void generateAvailabilityForNextInvocations(Machine machine) {
		long time = machine2Time.get(machine);
		if (time < amountOfSeconds) {
			MarkovGenerator<ObservationDiscrete> markovGenerator = machine2AvGenerator.get(machine);
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

			// para evitar que surjam eventos após o limite
			if (time + avDuration > amountOfSeconds) {
				avDuration = amountOfSeconds - time;
				avDuration = avDuration > 0 ? avDuration : 1;
			}
			AvailabilityRecord availabilityRecord = new AvailabilityRecord(machine.getName(), time, avDuration);
			this.nextAvailabilityRecords.add(availabilityRecord);

			// agora a duração do período de INdisponibilidade
			do {
				ObservationDiscrete observation = markovGenerator.observation();
				stateModel = (StateModel) observation.value;
				naDuration = stateModel.getDuration();
			} while (naDuration == 0 || stateModel.isAvailability());
			assert !stateModel.isAvailability() : stateModel;

			if (time + avDuration + naDuration > amountOfSeconds) {
				this.machine2Time.put(machine, amountOfSeconds);
			} else {
				this.machine2Time.put(machine, time + avDuration + naDuration);
			}
		} else {
			AvailabilityRecord availabilityRecord = new AvailabilityRecord(machine.getName(), amountOfSeconds + 1, 1);
			this.nextAvailabilityRecords.add(availabilityRecord);
		}
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
