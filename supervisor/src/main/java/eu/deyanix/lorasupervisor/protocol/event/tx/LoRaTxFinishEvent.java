package eu.deyanix.lorasupervisor.protocol.event.tx;

import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public abstract class LoRaTxFinishEvent extends LoRaNodeEvent {
	private final boolean successful;

	public LoRaTxFinishEvent(String name, boolean successful, LoRaPort port) {
		super(name, port);
		this.successful = successful;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
