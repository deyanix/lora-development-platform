package eu.deyanix.lorasupervisor.protocol.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;

public class LoRaConfiguration {
	private LoRaMode mode;
	private LoRaAuto auto;
	private Integer delta;
	private Long interval;
	private Boolean ackRequired;
	private Integer ackLifetime;
	private Boolean backOffIncrease;

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

	public Optional<Integer> getDelta() {
		return Optional.ofNullable(delta);
	}

	public LoRaConfiguration setDelta(int delta) {
		this.delta = delta;
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

	public Optional<Boolean> isBackOffIncreased() {
		return Optional.ofNullable(backOffIncrease);
	}

	public LoRaConfiguration setBackOffIncrease(Boolean backOffIncrease) {
		this.backOffIncrease = backOffIncrease;
		return this;
	}
}
