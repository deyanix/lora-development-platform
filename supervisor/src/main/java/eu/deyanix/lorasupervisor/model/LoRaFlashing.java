package eu.deyanix.lorasupervisor.model;

public class LoRaFlashing {
	private final boolean flashing;

	public LoRaFlashing(boolean flashing) {
		this.flashing = flashing;
	}

	public boolean isFlashing() {
		return flashing;
	}
}
