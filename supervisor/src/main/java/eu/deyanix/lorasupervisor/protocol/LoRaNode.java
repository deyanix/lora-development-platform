package eu.deyanix.lorasupervisor.protocol;

public class LoRaNode {
	private final String id;
	private final LoRaPort io;

	public LoRaNode(String id, LoRaPort io) {
		this.id = id;
		this.io = io;
	}

	public String getId() {
		return id;
	}

	public LoRaPort getPort() {
		return io;
	}
}
