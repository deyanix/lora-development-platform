package eu.deyanix.lorasupervisor.protocol.event.tx;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaTxBusyEvent extends LoRaTxFinishEvent {
	public LoRaTxBusyEvent(LoRaPort port) {
		super("TX_BUSY", false, port);
	}
}
