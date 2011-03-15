package br.edu.ufcg.lsd.oursim.util;

import java.util.Arrays;

public class ArrayBuilder {

	public static int[] createVector(int initialValue, int finalValue) {
		int[] vector = new int[(finalValue - initialValue) + 1];
		int index = 0;
		for (int i = initialValue; i <= finalValue; i++) {
			vector[index++] = i;
		}
		return vector;
	}

	public static int[] createVector(int value) {
		return createVector(value, value, value);
	}

	public static int[] createVector(int initialValue, int finalValue, int step) {
		Double vectorSizeD = Math.ceil(((finalValue - initialValue) + 1) / (step * 1.0));
		int vectorSize = vectorSizeD.intValue();
		// System.out.println(vectorSize);
		int[] vector = new int[vectorSize];
		int index = 0;
		for (int i = initialValue; i <= finalValue; i += step) {
			vector[index++] = i;
		}
		return vector;
	}

	public static void print(int[] vector) {
		System.out.println(Arrays.toString(vector));
	}

}
