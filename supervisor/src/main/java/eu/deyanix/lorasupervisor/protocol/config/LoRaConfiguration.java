package eu.deyanix.lorasupervisor.protocol.config;

public class LoRaConfiguration {
	public static final long FREQUENCY_MIN = 868_000_000;
	public static final long FREQUENCY_MAX = 870_000_000;
	public static final int SPREADING_FACTOR_MIN = 7;
	public static final int SPREADING_FACTOR_MAX = 12;
	public static final int POWER_MIN = -3;
	public static final int POWER_MAX = 22;
	public static final int PREAMBLE_LENGTH_MIN = 1;
	public static final int PREAMBLE_LENGTH_MAX = 8;
	public static final int TX_TIMEOUT_MIN = 0;
	public static final int TX_TIMEOUT_MAX = 0xFFFFFFFF;
	public static final int RX_TIMEOUT_MIN = 0;
	public static final int RX_TIMEOUT_MAX = 0xFFFF;

	private LoRaMode mode;
	private long frequency;
	private int power;
	private LoRaBandwidth bandwidth;
	private LoRaCodingRate codingRate;
	private int spreadingFactory;
	private int payloadLength;
	private int preambleLength;
	private boolean enableCrc;
	private boolean iqInverted;
	private int txTimeout;
	private int rxSymbolTimeout;
	private int minDelta;
	private int maxDelta;

	public LoRaMode getMode() {
		return mode;
	}

	public LoRaConfiguration setMode(LoRaMode mode) {
		this.mode = mode;
		return this;
	}

	public long getFrequency() {
		return frequency;
	}

	public LoRaConfiguration setFrequency(long frequency) {
		this.frequency = frequency;
		return this;
	}

	public int getPower() {
		return power;
	}

	public LoRaConfiguration setPower(int power) {
		this.power = power;
		return this;
	}

	public LoRaBandwidth getBandwidth() {
		return bandwidth;
	}

	public LoRaConfiguration setBandwidth(LoRaBandwidth bandwidth) {
		this.bandwidth = bandwidth;
		return this;
	}

	public LoRaCodingRate getCodingRate() {
		return codingRate;
	}

	public LoRaConfiguration setCodingRate(LoRaCodingRate codingRate) {
		this.codingRate = codingRate;
		return this;
	}

	public int getSpreadingFactory() {
		return spreadingFactory;
	}

	public LoRaConfiguration setSpreadingFactory(int spreadingFactory) {
		this.spreadingFactory = spreadingFactory;
		return this;
	}

	public int getPayloadLength() {
		return payloadLength;
	}

	public LoRaConfiguration setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
		return this;
	}

	public int getPreambleLength() {
		return preambleLength;
	}

	public LoRaConfiguration setPreambleLength(int preambleLength) {
		this.preambleLength = preambleLength;
		return this;
	}

	public boolean isEnableCrc() {
		return enableCrc;
	}

	public LoRaConfiguration setEnableCrc(boolean enableCrc) {
		this.enableCrc = enableCrc;
		return this;
	}

	public boolean isIqInverted() {
		return iqInverted;
	}

	public LoRaConfiguration setIqInverted(boolean enableIqInverted) {
		this.iqInverted = enableIqInverted;
		return this;
	}

	public int getTxTimeout() {
		return txTimeout;
	}

	public LoRaConfiguration setTxTimeout(int txTimeout) {
		this.txTimeout = txTimeout;
		return this;
	}

	public int getRxSymbolTimeout() {
		return rxSymbolTimeout;
	}

	public LoRaConfiguration setRxSymbolTimeout(int rxSymbolTimeout) {
		this.rxSymbolTimeout = rxSymbolTimeout;
		return this;
	}

	public int getMinDelta() {
		return minDelta;
	}

	public LoRaConfiguration setMinDelta(int minDelta) {
		this.minDelta = minDelta;
		return this;
	}

	public int getMaxDelta() {
		return maxDelta;
	}

	public LoRaConfiguration setMaxDelta(int maxDelta) {
		this.maxDelta = maxDelta;
		return this;
	}
}
