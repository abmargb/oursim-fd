package br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel;

import java.util.HashMap;
import java.util.Map;

import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;

public enum StateModelImp implements StateModel {

	AV_SHORT,

	AV_MED,

	AV_LONG,

	NA_SHORT_FROM_AV_SHORT,

	NA_LONG_FROM_AV_SHORT,

	NA_SHORT_FROM_AV_MED,

	NA_LONG_FROM_AV_MED,

	NA_SHORT_FROM_AV_LONG,

	NA_LONG_FROM_AV_LONG;

	private static Map<StateModelImp, Long> duration = new HashMap<StateModelImp, Long>();

	static {
		duration.put(AV_SHORT, 3 * MINUTE_DURATION);
		duration.put(AV_MED, 25 * MINUTE_DURATION);
		duration.put(AV_LONG, 300 * MINUTE_DURATION);
		duration.put(NA_SHORT_FROM_AV_SHORT, 7 * MINUTE_DURATION);
		duration.put(NA_LONG_FROM_AV_SHORT, 55 * MINUTE_DURATION);
		duration.put(NA_SHORT_FROM_AV_MED, 7 * MINUTE_DURATION);
		duration.put(NA_LONG_FROM_AV_MED, 55 * MINUTE_DURATION);
		duration.put(NA_SHORT_FROM_AV_LONG, 7 * MINUTE_DURATION);
		duration.put(NA_LONG_FROM_AV_LONG, 55 * MINUTE_DURATION);
	}

	private boolean isNAShort() {
		return this.equals(NA_SHORT_FROM_AV_SHORT) || this.equals(NA_SHORT_FROM_AV_MED) || this.equals(NA_SHORT_FROM_AV_LONG);
	}

	public ObservationDiscrete<StateModelImp> observation() {
		return new ObservationDiscrete<StateModelImp>(this);
	}

	public int getIndex() {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].equals(this)) {
				return i;
			}
		}
		throw new RuntimeException("Condição impossível: este estado não existe nesta enum.");
	}

	@Override
	public boolean isAvailability() {
		return this.equals(AV_SHORT) || this.equals(AV_MED) || this.equals(AV_LONG);
	}

	public static int numOfStates() {
		return values().length;
	}

	@Override
	public String getType() {
		return isAvailability() ? "AV_ST" : "NA_ST";
	}

	@Override
	public Long getDuration() {

		double coef = 0;
		double lambda = 0;
		if (isAvailability()) {
			if (this.equals(AV_SHORT)) {
				lambda = 3;
				coef = 0.32;
			} else if (this.equals(AV_MED)) {
				lambda = 25;
				coef = 0.44;
			} else if (this.equals(AV_LONG)) {
				lambda = 300;
				coef = 0.24;
			}
		} else {
			if (isNAShort()) {
				lambda = 7;
				coef = 0.68;
			} else {
				lambda = 55;
				coef = 0.32;
			}
		}

		ExponentialGen eg = new ExponentialGen(new MRG31k3p(), 1.0 / lambda);
		double sample = coef * eg.nextDouble();

		long eventDurantion = Math.round(sample) * MINUTE_DURATION;

		if (!isAvailability() && (eventDurantion < 7 * MINUTE_DURATION)) {
			return 7 * MINUTE_DURATION;
		}

		return eventDurantion;

	}

};