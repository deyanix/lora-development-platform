package eu.deyanix.lorasupervisor.protocol.config;

public class LoRaRadioConfiguration {
	public static final long FREQUENCY_MIN = 863_000_000;
	public static final long FREQUENCY_MAX = 870_000_000;
	public static final int SPREADING_FACTOR_MIN = 7;
	public static final int SPREADING_FACTOR_MAX = 12;
	public static final int POWER_MIN = -3;
	public static final int POWER_MAX = 22;
	public static final int PREAMBLE_LENGTH_MIN = 0x0001;
	public static final int PREAMBLE_LENGTH_MAX = 0xFFFF;;
	public static final int PAYLOAD_LENGTH_MIN = 0x00;
	public static final int PAYLOAD_LENGTH_MAX = 0xFF;
	public static final int TX_TIMEOUT_MIN = 0;
	public static final int TX_TIMEOUT_MAX = 0xFFFFFFFF;
	public static final int RX_TIMEOUT_MIN = 0;
	public static final int RX_TIMEOUT_MAX = 0xFFFF;

	private long frequency;
	private int power;
	private LoRaBandwidth bandwidth;
	private LoRaCodingRate codingRate;
	private int spreadingFactor;
	private int payloadLength;
	private int preambleLength;
	private boolean enableCrc;
	private boolean iqInverted;
	private int txTimeout;
	private int rxSymbolTimeout;

	public long getFrequency() {
		return frequency;
	}

	public LoRaRadioConfiguration setFrequency(long frequency) {
		this.frequency = frequency;
		return this;
	}

	public int getPower() {
		return power;
	}

	public LoRaRadioConfiguration setPower(int power) {
		this.power = power;
		return this;
	}

	public LoRaBandwidth getBandwidth() {
		return bandwidth;
	}

	public LoRaRadioConfiguration setBandwidth(LoRaBandwidth bandwidth) {
		this.bandwidth = bandwidth;
		return this;
	}

	public LoRaCodingRate getCodingRate() {
		return codingRate;
	}

	public LoRaRadioConfiguration setCodingRate(LoRaCodingRate codingRate) {
		this.codingRate = codingRate;
		return this;
	}

	public int getSpreadingFactor() {
		return spreadingFactor;
	}

	public LoRaRadioConfiguration setSpreadingFactor(int spreadingFactor) {
		this.spreadingFactor = spreadingFactor;
		return this;
	}

	public int getPayloadLength() {
		return payloadLength;
	}

	public LoRaRadioConfiguration setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
		return this;
	}

	public int getPreambleLength() {
		return preambleLength;
	}

	public LoRaRadioConfiguration setPreambleLength(int preambleLength) {
		this.preambleLength = preambleLength;
		return this;
	}

	public boolean isEnableCrc() {
		return enableCrc;
	}

	public LoRaRadioConfiguration setEnableCrc(boolean enableCrc) {
		this.enableCrc = enableCrc;
		return this;
	}

	public boolean isIqInverted() {
		return iqInverted;
	}

	public LoRaRadioConfiguration setIqInverted(boolean enableIqInverted) {
		this.iqInverted = enableIqInverted;
		return this;
	}

	public int getTxTimeout() {
		return txTimeout;
	}

	public LoRaRadioConfiguration setTxTimeout(int txTimeout) {
		this.txTimeout = txTimeout;
		return this;
	}

	public int getRxSymbolTimeout() {
		return rxSymbolTimeout;
	}

	public LoRaRadioConfiguration setRxSymbolTimeout(int rxSymbolTimeout) {
		this.rxSymbolTimeout = rxSymbolTimeout;
		return this;
	}
}
