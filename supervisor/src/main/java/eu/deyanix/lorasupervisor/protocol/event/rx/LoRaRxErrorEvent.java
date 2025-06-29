package eu.deyanix.lorasupervisor.protocol.event.rx;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaRxErrorEvent extends LoRaRxFinishEvent {
	public LoRaRxErrorEvent(LoRaPort port) {
		super("RX_ERROR", false, port);
	}
}
