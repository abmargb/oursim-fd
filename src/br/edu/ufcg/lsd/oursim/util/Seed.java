package br.edu.ufcg.lsd.oursim.util;

import java.util.Random;

public interface Seed {

	public static final Random PeerRankingPolicy_RANDOM = new Random(9354269l);

	// the first 3 values of the seed must all be less than m1 = 4294967087, and
	// not all 0; and the last 3 values must all be less than m2 = 4294944443,
	// and not all 0.
	public static final int[] OurGridAvailabilityCharacterization_SEED = new int[] { 1234, 13455, 5566, 6548, 8764, 5674 };

}
