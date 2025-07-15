package eu.deyanix.lorasupervisor.protocol.event.serial;

import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaSerialTxEvent extends LoRaEvent {
	private final String data;

	public LoRaSerialTxEvent(LoRaPort port, String data) {
		super("SERIAL_TX", port);
		this.data = data;
	}

	public String getData() {
		return data;
	}
}
