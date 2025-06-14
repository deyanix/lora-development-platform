package eu.deyanix.lorasupervisor.protocol.config;

public class LoRaDelta {
	private int min;
	private int max;

	public LoRaDelta(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public LoRaDelta setMin(int min) {
		this.min = min;
		return this;
	}

	public int getMax() {
		return max;
	}

	public LoRaDelta setMax(int max) {
		this.max = max;
		return this;
	}
}
