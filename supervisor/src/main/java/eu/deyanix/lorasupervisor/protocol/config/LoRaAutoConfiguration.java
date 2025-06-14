package eu.deyanix.lorasupervisor.protocol.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LoRaAutoConfiguration {
	private LoRaAuto auto;
	private int minDelta;
	private int maxDelta;
	private long interval;
	private boolean ackRequired;
	private int ackLifetime;

	public LoRaAuto getAuto() {
		return auto;
	}

	public LoRaAutoConfiguration setAuto(LoRaAuto auto) {
		this.auto = auto;
		return this;
	}

	public int getMinDelta() {
		return minDelta;
	}

	public LoRaAutoConfiguration setMinDelta(int minDelta) {
		this.minDelta = minDelta;
		return this;
	}

	public int getMaxDelta() {
		return maxDelta;
	}

	public LoRaAutoConfiguration setMaxDelta(int maxDelta) {
		this.maxDelta = maxDelta;
		return this;
	}

	@JsonIgnore
	public LoRaDelta getDelta() {
		return new LoRaDelta(minDelta, maxDelta);
	}

	public LoRaAutoConfiguration setDelta(LoRaDelta delta) {
		this.minDelta = delta.getMin();
		this.maxDelta = delta.getMax();
		return this;
	}

	public long getInterval() {
		return interval;
	}

	public LoRaAutoConfiguration setInterval(long interval) {
		this.interval = interval;
		return this;
	}

	public boolean isAckRequired() {
		return ackRequired;
	}

	public LoRaAutoConfiguration setAckRequired(boolean ackRequired) {
		this.ackRequired = ackRequired;
		return this;
	}

	public int getAckLifetime() {
		return ackLifetime;
	}

	public LoRaAutoConfiguration setAckLifetime(int ackLifetime) {
		this.ackLifetime = ackLifetime;
		return this;
	}
}
