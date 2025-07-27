package eu.deyanix.lorasupervisor.model;

import java.util.Collection;

public class LoRaEventStatistic {
	private final String nodeId;
	private final Collection<LoRaEventStatisticItem> stats;

	public LoRaEventStatistic(String nodeId, Collection<LoRaEventStatisticItem> stats) {
		this.nodeId = nodeId;
		this.stats = stats;
	}

	public String getNodeId() {
		return nodeId;
	}

	public Collection<LoRaEventStatisticItem> getStats() {
		return stats;
	}
}
