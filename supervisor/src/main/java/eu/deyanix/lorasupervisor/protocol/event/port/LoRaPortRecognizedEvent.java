package eu.deyanix.lorasupervisor.protocol.event.port;

import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaPortRecognizedEvent extends LoRaNodeEvent {
	public LoRaPortRecognizedEvent(LoRaPort port) {
		super("PORT_RECOGNIZED", port);
	}
}
