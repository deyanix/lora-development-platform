package eu.deyanix.lorasupervisor.protocol;

public class LoRaNode {
	private final String id;
	private final LoRaNodePort io;

	public LoRaNode(String id, LoRaNodePort io) {
		this.id = id;
		this.io = io;
	}

	public String getId() {
		return id;
	}

	public LoRaNodePort getPort() {
		return io;
	}
}
