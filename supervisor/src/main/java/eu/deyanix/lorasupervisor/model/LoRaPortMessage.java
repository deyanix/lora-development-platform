package eu.deyanix.lorasupervisor.model;

public class LoRaPortMessage {
	private final String port;
	private final String message;

	public LoRaPortMessage(String port, String message) {
		this.port = port;
		this.message = message;
	}

	public String getPort() {
		return port;
	}

	public String getMessage() {
		return message;
	}
}
