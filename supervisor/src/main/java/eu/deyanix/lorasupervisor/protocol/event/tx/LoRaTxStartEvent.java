package eu.deyanix.lorasupervisor.protocol.event.tx;

import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaTxStartEvent extends LoRaNodeEvent {
	private final String data;

	public LoRaTxStartEvent(LoRaPort port, String data) {
		super("TX_START", port);
		this.data = data;
	}

	public String getData() {
		return data;
	}
}
