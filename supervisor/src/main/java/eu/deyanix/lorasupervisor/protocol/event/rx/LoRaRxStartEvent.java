package eu.deyanix.lorasupervisor.protocol.event.rx;

import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaRxStartEvent extends LoRaNodeEvent {
	public LoRaRxStartEvent(LoRaPort port) {
		super("RX_START", port);
	}
}
