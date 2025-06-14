package eu.deyanix.lorasupervisor.protocol.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;

public class LoRaConfiguration {
	private LoRaMode mode;
	private LoRaAuto auto;
	private Integer minDelta;
	private Integer maxDelta;
	private Long interval;
	private Boolean ackRequired;
	private Integer ackLifetime;

	public Optional<LoRaMode> getMode() {
		return Optional.ofNullable(mode);
	}

	public LoRaConfiguration setMode(LoRaMode mode) {
		this.mode = mode;
		return this;
	}

	public Optional<LoRaAuto> getAuto() {
		return Optional.ofNullable(auto);
	}

	public LoRaConfiguration setAuto(LoRaAuto auto) {
		this.auto = auto;
		return this;
	}

	public Optional<Integer> getMinDelta() {
		return Optional.ofNullable(minDelta);
	}

	public LoRaConfiguration setMinDelta(Integer minDelta) {
		this.minDelta = minDelta;
		return this;
	}

	public Optional<Integer> getMaxDelta() {
		return Optional.ofNullable(maxDelta);
	}

	public LoRaConfiguration setMaxDelta(Integer maxDelta) {
		this.maxDelta = maxDelta;
		return this;
	}

	@JsonIgnore
	public Optional<LoRaDelta> getDelta() {
		if (minDelta == null || maxDelta == null) {
			return Optional.empty();
		}
		return Optional.of(new LoRaDelta(minDelta, maxDelta));
	}

	public LoRaConfiguration setDelta(LoRaDelta delta) {
		this.minDelta = delta.getMin();
		this.maxDelta = delta.getMax();
		return this;
	}

	public Optional<Long> getInterval() {
		return Optional.ofNullable(interval);
	}

	public LoRaConfiguration setInterval(Long interval) {
		this.interval = interval;
		return this;
	}

	public Optional<Boolean> isAckRequired() {
		return Optional.ofNullable(ackRequired);
	}

	public LoRaConfiguration setAckRequired(Boolean ackRequired) {
		this.ackRequired = ackRequired;
		return this;
	}

	public Optional<Integer> getAckLifetime() {
		return Optional.ofNullable(ackLifetime);
	}

	public LoRaConfiguration setAckLifetime(Integer ackLifetime) {
		this.ackLifetime = ackLifetime;
		return this;
	}
}
