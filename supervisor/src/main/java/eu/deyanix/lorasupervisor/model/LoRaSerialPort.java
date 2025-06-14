package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;

public class LoRaSerialPort {
	private String port;
	private String nodeId;

	public String getPort() {
		return port;
	}

	public LoRaSerialPort setPort(String port) {
		this.port = port;
		return this;
	}

	public String getNodeId() {
		return nodeId;
	}

	public LoRaSerialPort setNodeId(String nodeId) {
		this.nodeId = nodeId;
		return this;
	}
}
