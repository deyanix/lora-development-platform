package eu.deyanix.lorasupervisor.protocol.event.rx;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaRxTimeoutEvent extends LoRaRxFinishEvent {
	public LoRaRxTimeoutEvent(LoRaPort port) {
		super("RX_TIMEOUT", false, port);
	}
}
