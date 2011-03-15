/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package br.edu.ufcg.lsd.oursim.io.input.availability.markovmodel;

import java.io.BufferedWriter;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;

public class HostAvailabilityGeneratorImp extends HostAvailabilityGenerator {

	public HostAvailabilityGeneratorImp(String machineName, int quantHoras, BufferedWriter bw, boolean recordAll) {
		super(machineName, quantHoras, bw, recordAll);
	}

	@SuppressWarnings("unchecked")
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
