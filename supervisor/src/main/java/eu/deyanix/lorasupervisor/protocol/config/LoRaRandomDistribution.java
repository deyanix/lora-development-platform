package eu.deyanix.lorasupervisor.protocol.config;

public enum LoRaRandomDistribution {
	UNIFORM("UNI"),
	NORMAL("NOR"),
	EXPONENTIAL("EXP");

	public static LoRaRandomDistribution getEnum(String value) {
		for (LoRaRandomDistribution val : values()) {
			if (val.shortName.equals(value)) {
				return val;
			}
		}
		return null;
	}

	private final String shortName;

	LoRaRandomDistribution(String shortName) {
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
