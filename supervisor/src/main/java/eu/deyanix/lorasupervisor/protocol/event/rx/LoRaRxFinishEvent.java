package eu.deyanix.lorasupervisor.protocol.event.rx;

import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public abstract class LoRaRxFinishEvent extends LoRaNodeEvent {
	private final boolean successful;

	public LoRaRxFinishEvent(String name, boolean successful, LoRaPort port) {
		super(name, port);
		this.successful = successful;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
