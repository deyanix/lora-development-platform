package eu.deyanix.lorasupervisor.protocol.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaNodeEvent extends LoRaEvent {
	private final LoRaNode node;

	public LoRaNodeEvent(String name, LoRaPort port) {
		super(name, port);
		node = port.getNode();
	}

	@JsonIgnore
	public LoRaNode getNode() {
		return node;
	}

	public String getNodeId() {
		if (node == null) {
			return null;
		}

		return node.getId();
	}
}
