package eu.deyanix.lorasupervisor.model;

public class LoRaEventSearchCriteria {
	private int length = 100;
	private int offset = 0;

	public int getOffset() {
		return offset;
	}

	public LoRaEventSearchCriteria setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public int getLength() {
		return length;
	}

	public LoRaEventSearchCriteria setLength(int length) {
		this.length = length;
		return this;
	}
}
