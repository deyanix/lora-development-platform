package eu.deyanix.lorasupervisor.protocol.event;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class LoRaEvent implements Comparable<LoRaEvent> {
	private static final AtomicLong counter = new AtomicLong();
	private final long id;
	private final String name;
	private final LocalDateTime date;
	private final String portName;

	public LoRaEvent(String name, LoRaPort port) {
		this.id = counter.incrementAndGet();
		this.name = name;
		this.date = LocalDateTime.now();
		this.portName = port.getSerialPort().getSystemPortName();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getPortName() {
		return portName;
	}

	@Override
	public int compareTo(LoRaEvent o) {
		int dateComparison = o.date.compareTo(this.date);
		if (dateComparison != 0) {
			return dateComparison;
		}

		return Long.compare(this.id, o.id);
	}
}
