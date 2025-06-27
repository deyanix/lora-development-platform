package eu.deyanix.lorasupervisor.protocol.config;

import java.util.Optional;

public class LoRaConfiguration {
	private LoRaMode mode;
	private LoRaAuto auto;
	private Integer initialBackoffMax;
	private Long interval;
	private Boolean ackRequired;
	private Integer ackLifetime;
	private Boolean backoffIncrease;

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

	public Optional<Integer> getInitialBackoffMax() {
		return Optional.ofNullable(initialBackoffMax);
	}

	public LoRaConfiguration setInitialBackoffMax(int initialBackoffMax) {
		this.initialBackoffMax = initialBackoffMax;
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

	public Optional<Boolean> isBackoffIncrease() {
		return Optional.ofNullable(backoffIncrease);
	}

	public LoRaConfiguration setBackoffIncrease(Boolean backoffIncrease) {
		this.backoffIncrease = backoffIncrease;
		return this;
	}

	public LoRaConfiguration merge(LoRaConfiguration other) {
		other.getMode().ifPresent(this::setMode);
		other.getInitialBackoffMax().ifPresent(this::setInitialBackoffMax);
		other.getAuto().ifPresent(this::setAuto);
		other.isAckRequired().ifPresent(this::setAckRequired);
		other.getAckLifetime().ifPresent(this::setAckLifetime);
		other.getInterval().ifPresent(this::setInterval);
		other.isBackoffIncrease().ifPresent(this::setBackoffIncrease);
		return this;
	}
}
