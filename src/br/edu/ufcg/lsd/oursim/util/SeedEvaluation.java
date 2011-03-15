package br.edu.ufcg.lsd.oursim.util;

import umontreal.iro.lecuyer.randvar.LognormalGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;

public class SeedEvaluation {

	public static void main(String[] args) {
		logNormalOurgrid();
	}

	private static void logNormalOurgrid() {
		// the first 3 values of the seed must all be less than m1 = 4294967087,
		// and not all 0; and the last 3 values must all be less than m2 =
		// 4294944443, and not all 0.
		MRG31k3p.setPackageSeed(new int[] { 1234, 13455, 5566, 6548, 8764, 5674 });
		MRG31k3p g1 = new MRG31k3p();
		MRG31k3p g2 = new MRG31k3p();
		MRG31k3p g3 = new MRG31k3p();
		MRG31k3p g4 = new MRG31k3p();
		LognormalGen l1 = new LognormalGen(g1, 7.957307, 2.116613);
		LognormalGen l2 = new LognormalGen(g2, 7.957307, 2.116613);
		LognormalGen l3 = new LognormalGen(g3, 7.957307, 2.116613);
		LognormalGen l4 = new LognormalGen(g4, 7.957307, 2.116613);

		for (int i = 0; i < 2; i++) {
			System.out.println(l1.nextDouble());
			System.out.println(l2.nextDouble());
			System.out.println(l3.nextDouble());
			System.out.println(l4.nextDouble());
			System.out.println("-------------");
		}
	}
	
}
