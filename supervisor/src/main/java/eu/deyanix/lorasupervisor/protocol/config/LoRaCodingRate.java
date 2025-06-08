package eu.deyanix.lorasupervisor.protocol.config;

public enum LoRaCodingRate {
	RATE_4_5(4, 5, 1),
	RATE_4_6(4, 6, 2),
	RATE_4_7(4, 7, 3),
	RATE_4_8(4, 8, 4);

	private final int informationBits;
	private final int bits;
	private final int value;

	LoRaCodingRate(int informationBits, int bits, int value) {
		this.informationBits = informationBits;
		this.bits = bits;
		this.value = value;
	}

	public int getInformationBits() {
		return informationBits;
	}

	public int getCorrectionBits() {
		return bits - informationBits;
	}

	public int getBits() {
		return bits;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return informationBits + "/" + bits;
	}
}
