package eu.deyanix.lorasupervisor.protocol.event.tx;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaTxDoneEvent extends LoRaTxFinishEvent {
	private final long duration;

	public LoRaTxDoneEvent(LoRaPort port, long duration) {
		super("TX_DONE", true, port);
		this.duration = duration;
	}

	public long getDuration() {
		return duration;
	}
}
