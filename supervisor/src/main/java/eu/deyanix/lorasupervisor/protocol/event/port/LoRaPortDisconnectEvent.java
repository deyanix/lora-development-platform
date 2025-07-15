package eu.deyanix.lorasupervisor.protocol.event.port;

import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaPortDisconnectEvent extends LoRaNodeEvent {
	public LoRaPortDisconnectEvent(LoRaPort port) {
		super("PORT_DISCONNECT", port);
	}
}
