package eu.deyanix.lorasupervisor.protocol.event.port;

import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaPortConnectEvent extends LoRaNodeEvent {
	public LoRaPortConnectEvent(LoRaPort port) {
		super("PORT_CONNECT", port);
	}
}
