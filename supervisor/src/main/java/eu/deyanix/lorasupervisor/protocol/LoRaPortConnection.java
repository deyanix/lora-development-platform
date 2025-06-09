package eu.deyanix.lorasupervisor.protocol;

import java.time.Duration;
import java.time.LocalDateTime;

public class LoRaPortConnection {
	private final LoRaNodePort port;
	private final int priority;
	private int requestedData = 0;

	public LoRaPortConnection(LoRaNodePort port, int priority) {
		this.port = port;
		this.priority = priority;
	}

	public LoRaPortConnection(LoRaNodePort port) {
		this(port, 0);
	}
}
