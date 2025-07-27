package eu.deyanix.lorasupervisor.model;

public class LoRaEventStatisticItem {
	private final String name;
	private final int count;

	public LoRaEventStatisticItem(String name, int count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}
}
