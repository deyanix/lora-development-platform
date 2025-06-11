package eu.deyanix.lorasupervisor.protocol;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaNode {
	private final String id;
	private final LoRaPort port;

	public LoRaNode(String id, LoRaPort port) {
		this.id = id;
		this.port = port;
	}

	public String getId() {
		return id;
	}

	public LoRaPort getPort() {
		return port;
	}
}
