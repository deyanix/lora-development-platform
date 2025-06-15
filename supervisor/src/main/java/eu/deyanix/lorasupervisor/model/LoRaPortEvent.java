package eu.deyanix.lorasupervisor.model;

public class LoRaPortEvent {
	private final String portName;

	public LoRaPortEvent(String portName) {
		this.portName = portName;
	}

	public String getPortName() {
		return portName;
	}
}
