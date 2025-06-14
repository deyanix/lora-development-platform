package eu.deyanix.lorasupervisor.model;

public class LoRaPortEvent {
	private final String port;

	public LoRaPortEvent(String port) {
		this.port = port;
	}

	public String getPort() {
		return port;
	}
}
