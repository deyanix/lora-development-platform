package eu.deyanix.lorasupervisor.protocol.config;

public enum LoRaMode {
	SRC("Source", 0),
	SNK("Sink", 1);

	private final String fullName;
	private final int value;

	LoRaMode(String fullName, int value) {
		this.fullName = fullName;
		this.value = value;
	}

	public String getFullName() {
		return fullName;
	}

	public int getValue() {
		return value;
	}

	public static LoRaMode valueOf(int value) {
		for (LoRaMode mode : values()) {
			if (mode.getValue() == value) {
				return mode;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return fullName;
	}
}
