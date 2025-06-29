package eu.deyanix.lorasupervisor.protocol.event.tx;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaTxTimeoutEvent extends LoRaTxFinishEvent {
	public LoRaTxTimeoutEvent(LoRaPort port) {
		super("TX_TIMEOUT", false, port);
	}
}
