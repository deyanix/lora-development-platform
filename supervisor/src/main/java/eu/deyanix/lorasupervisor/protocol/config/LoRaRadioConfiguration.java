package eu.deyanix.lorasupervisor.protocol.config;

import java.util.Optional;

public class LoRaRadioConfiguration {
	public static final long FREQUENCY_MIN = 863_000_000;
	public static final long FREQUENCY_MAX = 870_000_000;
	public static final int SPREADING_FACTOR_MIN = 7;
	public static final int SPREADING_FACTOR_MAX = 12;
	public static final int POWER_MIN = -3;
	public static final int POWER_MAX = 22;
	public static final int PREAMBLE_LENGTH_MIN = 0x0001;
	public static final int PREAMBLE_LENGTH_MAX = 0xFFFF;
	public static final int PAYLOAD_LENGTH_MIN = 0x00;
	public static final int PAYLOAD_LENGTH_MAX = 0xFF;
	public static final int TX_TIMEOUT_MIN = 0;
	public static final int TX_TIMEOUT_MAX = 0xFFFFFFFF;
	public static final int RX_TIMEOUT_MIN = 0;
	public static final int RX_TIMEOUT_MAX = 0xFFFF;

	private Long frequency;
	private Integer power;
	private LoRaBandwidth bandwidth;
	private LoRaCodingRate codingRate;
	private Integer spreadingFactor;
	private Integer payloadLength;
	private Integer preambleLength;
	private Boolean enableCrc;
	private Boolean iqInverted;
	private Integer txTimeout;
	private Integer rxSymbolTimeout;

	public Optional<Long> getFrequency() {
		return Optional.ofNullable(frequency);
	}

	public LoRaRadioConfiguration setFrequency(Long frequency) {
		this.frequency = frequency;
		return this;
	}

	public Optional<Integer> getPower() {
		return Optional.ofNullable(power);
	}

	public LoRaRadioConfiguration setPower(Integer power) {
		this.power = power;
		return this;
	}

	public Optional<LoRaBandwidth> getBandwidth() {
		return Optional.ofNullable(bandwidth);
	}

	public LoRaRadioConfiguration setBandwidth(LoRaBandwidth bandwidth) {
		this.bandwidth = bandwidth;
		return this;
	}

	public Optional<LoRaCodingRate> getCodingRate() {
		return Optional.ofNullable(codingRate);
	}

	public LoRaRadioConfiguration setCodingRate(LoRaCodingRate codingRate) {
		this.codingRate = codingRate;
		return this;
	}

	public Optional<Integer> getSpreadingFactor() {
		return Optional.ofNullable(spreadingFactor);
	}

	public LoRaRadioConfiguration setSpreadingFactor(Integer spreadingFactor) {
		this.spreadingFactor = spreadingFactor;
		return this;
	}

	public Optional<Integer> getPayloadLength() {
		return Optional.ofNullable(payloadLength);
	}

	public LoRaRadioConfiguration setPayloadLength(Integer payloadLength) {
		this.payloadLength = payloadLength;
		return this;
	}

	public Optional<Integer> getPreambleLength() {
		return Optional.ofNullable(preambleLength);
	}

	public LoRaRadioConfiguration setPreambleLength(Integer preambleLength) {
		this.preambleLength = preambleLength;
		return this;
	}

	public Optional<Boolean> isEnableCrc() {
		return Optional.ofNullable(enableCrc);
	}

	public LoRaRadioConfiguration setEnableCrc(Boolean enableCrc) {
		this.enableCrc = enableCrc;
		return this;
	}

	public Optional<Boolean> isIqInverted() {
		return Optional.ofNullable(iqInverted);
	}

	public LoRaRadioConfiguration setIqInverted(Boolean enableIqInverted) {
		this.iqInverted = enableIqInverted;
		return this;
	}

	public Optional<Integer> getTxTimeout() {
		return Optional.ofNullable(txTimeout);
	}

	public LoRaRadioConfiguration setTxTimeout(Integer txTimeout) {
		this.txTimeout = txTimeout;
		return this;
	}

	public Optional<Integer> getRxSymbolTimeout() {
		return Optional.ofNullable(rxSymbolTimeout);
	}

	public LoRaRadioConfiguration setRxSymbolTimeout(Integer rxSymbolTimeout) {
		this.rxSymbolTimeout = rxSymbolTimeout;
		return this;
	}

	public LoRaRadioConfiguration merge(LoRaRadioConfiguration other) {
		other.getFrequency().ifPresent(this::setFrequency);
		other.getBandwidth().ifPresent(this::setBandwidth);
		other.getPower().ifPresent(this::setPower);
		other.getSpreadingFactor().ifPresent(this::setSpreadingFactor);
		other.getCodingRate().ifPresent(this::setCodingRate);
		other.isEnableCrc().ifPresent(this::setEnableCrc);
		other.isIqInverted().ifPresent(this::setIqInverted);
		other.getPreambleLength().ifPresent(this::setPreambleLength);
		other.getPayloadLength().ifPresent(this::setPayloadLength);
		other.getTxTimeout().ifPresent(this::setTxTimeout);
		other.getRxSymbolTimeout().ifPresent(this::setRxSymbolTimeout);
		return this;
	}
}
