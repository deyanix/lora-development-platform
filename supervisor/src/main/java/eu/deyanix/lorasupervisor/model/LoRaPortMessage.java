package eu.deyanix.lorasupervisor.model;

public class LoRaPortMessage extends LoRaPortEvent {
	private final String message;

	public LoRaPortMessage(String port, String message) {
		super(port);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
