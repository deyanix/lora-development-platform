package eu.deyanix.lorasupervisor.protocol.config;

public enum LoRaMode {
	SOURCE("SRC"),
	SINK("SNK");

	public static LoRaMode getEnum(String value) {
		for (LoRaMode val : values()) {
			if (val.shortName.equals(value)) {
				return val;
			}
		}
		return null;
	}

	private final String shortName;

	LoRaMode(String shortName) {
		this.shortName = shortName;
	}

	public String getValue() {
		return shortName;
	}

	@Override
	public String toString() {
		return name();
	}
}
