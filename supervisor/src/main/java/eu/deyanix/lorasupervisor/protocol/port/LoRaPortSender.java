package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.event.serial.LoRaSerialTxEvent;

import java.io.PrintStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoRaPortSender {
	private final LoRaPort port;
	private final PrintStream out;
	private final Lock writeLock = new ReentrantLock();

	public LoRaPortSender(LoRaPort port) {
		this.port = port;
		this.out = new PrintStream(port.getSerialPort().getOutputStream());
	}

	public void send(String data) {
		writeLock.lock();
		try {
			out.print(data);

			port.invokeEvent(new LoRaSerialTxEvent(port, data));
		} finally {
			writeLock.unlock();
		}
	}
}
