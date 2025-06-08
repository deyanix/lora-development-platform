package eu.deyanix.lorasupervisor.protocol.config;

public class LoRaConfiguration {
	private LoRaMode mode;
	private long frequency;
	private int power;
	private LoRaBandwidth bandwidth;
	private LoRaCodingRate codingRate;
	private int spreadingFactory;
	private int payloadLength;
	private int preambleLength;
	private boolean enableCrc;
	private boolean enableIqInverted;
	private int txTimeout;
	private int rxSymbolTimeout;

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

	public boolean isEnableIqInverted() {
		return enableIqInverted;
	}

	public LoRaConfiguration setEnableIqInverted(boolean enableIqInverted) {
		this.enableIqInverted = enableIqInverted;
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
}
