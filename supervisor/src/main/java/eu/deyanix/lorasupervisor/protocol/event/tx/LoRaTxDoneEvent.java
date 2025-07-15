package eu.deyanix.lorasupervisor.protocol.event.tx;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaTxDoneEvent extends LoRaTxFinishEvent {
	public LoRaTxDoneEvent(LoRaPort port) {
		super("TX_DONE", true, port);
	}
}
