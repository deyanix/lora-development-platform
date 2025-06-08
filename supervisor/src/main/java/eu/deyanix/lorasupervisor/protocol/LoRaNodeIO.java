package eu.deyanix.lorasupervisor.protocol;

import com.fazecast.jSerialComm.SerialPort;

import java.time.Duration;
import java.time.LocalDateTime;

public class LoRaNodeIO {
	public static final int NODE_BAUD_RATE = 115200;

	public static LoRaNodeIO openNode(SerialPort port) {
		if (!port.isOpen() && !port.openPort()) {
			return null;
		}
		port.setBaudRate(NODE_BAUD_RATE);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		return new LoRaNodeIO(port);
	}

	private final SerialPort port;
	private Duration timeout = Duration.ofSeconds(1);
	private LocalDateTime lastActivity = LocalDateTime.MIN;
	private StringBuffer buffer = new StringBuffer();

	public LoRaNodeIO(SerialPort serialPort) {
		this.port = serialPort;
	}

	public SerialPort getPort() {
		return port;
	}

	public Duration getTimeout() {
		return timeout;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}


}
