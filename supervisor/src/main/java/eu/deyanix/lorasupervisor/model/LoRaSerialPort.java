package eu.deyanix.lorasupervisor.model;

public class LoRaSerialPort {
	private String portName;
	private String nodeId;

	public String getPortName() {
		return portName;
	}

	public LoRaSerialPort setPortName(String portName) {
		this.portName = portName;
		return this;
	}

	public String getNodeId() {
		return nodeId;
	}

	public LoRaSerialPort setNodeId(String nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public boolean isConnected() {
		return nodeId != null;
	}
}
