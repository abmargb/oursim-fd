package br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel;

import java.io.BufferedWriter;
import java.io.IOException;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;

public abstract class HostAvailabilityGenerator {

	public static final String TAB = "\t\t";
	public static final String EOL = "\n";
	public static final double _00 = 0.0;

	private String machineName;
	private long quantDeSegundos;
	private boolean recordAll = false;

	private BufferedWriter bw;
	
	public HostAvailabilityGenerator(String machineName, int quantHoras, BufferedWriter bw, boolean recordAll) {
		this.bw = bw;
		this.machineName = machineName;
		this.quantDeSegundos = quantHoras * 60 * 60;
		this.recordAll = recordAll;
	}

	@SuppressWarnings("unchecked")
	protected abstract Hmm<ObservationDiscrete> buildHmm();

	@SuppressWarnings("unchecked")
	public void generateAvailabilityObservations() {

		Hmm<ObservationDiscrete> hmm = buildHmm();

		MarkovGenerator<ObservationDiscrete> mg = new MarkovGenerator<ObservationDiscrete>(hmm);

		long time = 0;

		while (time < quantDeSegundos) {
			ObservationDiscrete observation = mg.observation();
			StateModel stateModel = (StateModel) observation.value;
			Long duracao = stateModel.getDuration();
			System.out.println(machineName+" : "+time+" : " +stateModel.getType()+" : " +duracao);
			if (recordAll || stateModel.isAvailability()) {
//				sb.append(machineName).append(TAB)
//				  .append(time).append(TAB)
//				  .append(duracao).append(TAB)
//				  .append(stateModel.getType()).append(TAB)
//				  .append(stateModel).append(EOL);
				
				try {
					StringBuilder buffer = new StringBuilder();
					buffer.append(machineName).append(TAB)
						  .append(time).append(TAB)
						  .append(duracao).append(EOL);
					bw.append(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			time += duracao;
		}

	}

}