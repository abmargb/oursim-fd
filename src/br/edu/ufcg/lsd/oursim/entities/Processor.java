package br.edu.ufcg.lsd.oursim.entities;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * A Central Processing Unit (CPU) defined in terms of Millions Instructions Per
 * Second (MIPS) rating.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 22/04/2010
 * 
 */
public class Processor implements Comparable<Processor> {

	/**
	 * EC2 Compute Unit (ECU) – One EC2 Compute Unit (ECU) provides the
	 * equivalent CPU capacity of a 1.0-1.2 GHz 2007 Opteron or 2007 Xeon
	 * processor.
	 * 
	 * <pre>
	 * 
	 * 		This is really simple, yet Amazon manages to totally obfuscate it. Yes, a compute unit refers to one core. Here's what you actually get:
	 * 		
	 * 		Small: 50% time-share of 1 core at &tilde;2.4GHz (effectively &tilde;1.2GHz)
	 * 		Large: 2 cores at &tilde;2.4GHz
	 * 		XLarge: 4 cores at &tilde;2.4GHz
	 * 		High Med: 2 cores at &tilde;3GHz
	 * 		High XL: 8 cores at &tilde;3GHz
	 * 		
	 * 		
	 * 		Pentium 4 EE: 7500 to 11000 MIPS
	 * 		
	 * 		Original Pentium: Variants
	 * 
	 * 60 MHz with 100 MIPS (70.4 SPECint92, 55.1 SPECfp92 on Xpress 256 KB L2)
	 * 66 MHz with 112 MIPS (77.9 SPECint92, 63.6 SPECfp92 on Xpress 256 KB L2)
	 * 
	 * 	Results on a 2.4 GHz Core 2 Duo (1 CPU 2007) vary from 9.7 MWIPS using BASIC Interpreter, 59 MWIPS via BASIC Compiler, 347 MWIPS using 1987 Fortran, 1,534 MWIPS through HTML/Java to 2,403 MWIPS using a modern C/C++ compiler.
	 * 		
	 * </pre>
	 */
	// public static Processor EC2_COMPUTE_UNIT = new Processor(0, 1000000);
	public static Processor EC2_COMPUTE_UNIT = new Processor(0, convertGHz2Mips(1.0));

	public static long convertGHz2Mips(double nGHz) {
		long oneGHzInMips = 3000;
		return Math.round(nGHz * oneGHzInMips);
	}

	/**
	 * the identifier of this processor.
	 */
	private final int id;

	/**
	 * The rating in SPEC MIPS or LINPACK MFLOPS of this processor (MIPSRating).
	 */
	private final long speed;

	/**
	 * Flag indicating of this processor is busy.
	 */
	private boolean busy = false;

	/**
	 * the machine this processor belongs to.
	 */
	private Machine machine;

	/**
	 * An ordinary constructor for a processor.
	 * 
	 * @param id
	 *            the processor's id.
	 * @param speed
	 *            the rating of this processor.
	 */
	Processor(int id, long speed) {
		this(id, speed, null);
	}

	/**
	 * An ordinary constructor for a processor.
	 * 
	 * @param id
	 *            the processor's id.
	 * @param speed
	 *            the rating of this processor.
	 * @param machine
	 *            the machine this processor belongs to.
	 */
	Processor(int id, long speed, Machine machine) {
		this.id = id;
		this.speed = speed;
		this.machine = machine;
	}

	/**
	 * @return the processor's id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the rating of this processor.
	 */
	public long getSpeed() {
		return speed;
	}

	/**
	 * Verifies if this processor is busy.
	 * 
	 * @return <code>true</true> if this processor is busy, <code>false</code> otherwise.
	 */
	public boolean isBusy() {
		return busy;
	}

	/**
	 * Indicates that this processor is busy.
	 */
	public void busy() {
		this.busy = true;
	}

	/**
	 * Indicates that this processor is free.
	 */
	public void free() {
		this.busy = false;
	}

	/**
	 * Calculate the number of instructions that could be processed by this
	 * processor in a given amount of time.This method could be seem as the
	 * opposite of {@link #calculateTimeToExecute(long).
	 * 
	 * @param duration
	 *            the amount of time in which is going to be calculated the
	 *            number of instructions.
	 * @return the number of instructions that could be processed by this
	 *         processor in a given amount of time.
	 * @throws IllegalArgumentException
	 *             if <code>duration < 1</code>.
	 * @see {@link #calculateTimeToExecute(long)}
	 */
	public long calculateNumberOfInstructionsProcessed(long duration) throws IllegalArgumentException {
		assert duration > 0;
		if (duration < 1) {
			throw new IllegalArgumentException("duration must be at least 1.");
		}
		return speed * duration;
	}

	/**
	 * Calculate the amount of time needed to processed a given number of
	 * instructions. This method could be seem as the opposite of
	 * {@link #calculateNumberOfInstructionsProcessed(long)}.
	 * 
	 * @param numberOfInstruction
	 *            the number of instructions to be processed.
	 * @return the amount of time needed to processed a given number of
	 *         instructions.
	 * @throws IllegalArgumentException
	 *             if <code>numberOfInstruction < 1</code>.
	 * @see {@link #calculateNumberOfInstructionsProcessed(long)}
	 */
	public long calculateTimeToExecute(long numberOfInstruction) throws IllegalArgumentException {
		assert numberOfInstruction > 0 : numberOfInstruction + " > 0";
		if (numberOfInstruction < 1) {
			throw new IllegalArgumentException("numberOfInstruction must be at least 1.");
		}
		// it must be casted to a double value because "/" truncates the result
		double estimatedFinishTimeD = (double) numberOfInstruction / speed;
		long estimatedFinishTimeL = (long) estimatedFinishTimeD;
		long adjustment = (estimatedFinishTimeL < estimatedFinishTimeD) ? 1 : 0;
		return estimatedFinishTimeL + adjustment;
	}

	public Machine getMachine() {
		return machine;
	}

	@Override
	public int compareTo(Processor o) {
		return (this.machine.getName() + this.id).compareTo(o.machine.getName() + o.id);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("speed", speed).append("busy", busy).append("machine",
				machine.getName()).toString();
	}
	
	

}
