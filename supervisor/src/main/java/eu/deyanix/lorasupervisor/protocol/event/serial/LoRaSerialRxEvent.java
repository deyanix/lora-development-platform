package eu.deyanix.lorasupervisor.protocol.event.serial;

import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaSerialRxEvent extends LoRaEvent {
	private final String data;

	public LoRaSerialRxEvent(LoRaPort port, String data) {
		super("SERIAL_RX", port);
		this.data = data;
	}

	public String getData() {
		return data;
	}
}
