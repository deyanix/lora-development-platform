package eu.deyanix.lorasupervisor.protocol.config;

public enum LoRaBandwidth {
	BW_125kHz(125, 0),
	BW_250kHz(250, 1),
	BW_500kHz(500, 2);

	private final int bandwidth;
	private final int value;

	LoRaBandwidth(int bandwidth, int value) {
		this.bandwidth = bandwidth;
		this.value = value;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return bandwidth + "kHz";
	}
}
