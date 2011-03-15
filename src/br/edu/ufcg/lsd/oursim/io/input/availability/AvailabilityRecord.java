package br.edu.ufcg.lsd.oursim.io.input.availability;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * An ordinary record for a machine availability event.
 * 
 * @author Edigley P. Fraga, edigley@lsd.ufcg.edu.br
 * @since 25/05/2010
 * 
 */
public class AvailabilityRecord implements Comparable<AvailabilityRecord> {

	/**
	 * the name of the machine this record relates to.
	 */
	private String machineName;

	/**
	 * the time from which the resource will be make available.
	 */
	private long time;

	/**
	 * the duration of the availability period.
	 */
	private long duration;

	/**
	 * An ordinary constructor for a machine availability record event.
	 * 
	 * @param machineName
	 *            the name of the machine this record relates to.
	 * @param timestamp
	 *            the time from which the resource will be make available.
	 * @param duration
	 *            the duration of the availability period.
	 */
	public AvailabilityRecord(String machineName, long timestamp, long duration) {
		assert timestamp >= 0;
		assert duration > 0;
		this.machineName = machineName;
		this.time = timestamp;
		this.duration = duration;
	}

	/**
	 * @return the name of the machine this record relates to.
	 */
	public String getMachineName() {
		return machineName;
	}

	/**
	 * @return the time from which the resource will be make available.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return the duration of the availability period.
	 */
	public long getDuration() {
		return duration;
	}

	@Override
	public int compareTo(AvailabilityRecord o) {
		return (int) (this.time - o.time);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("machineName", machineName).append("time", time).append("duration", duration)
				.toString();
	}
	
	

}
