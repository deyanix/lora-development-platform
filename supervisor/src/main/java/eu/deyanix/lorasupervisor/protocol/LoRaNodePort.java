package eu.deyanix.lorasupervisor.protocol;

import com.fazecast.jSerialComm.SerialPort;
import eu.deyanix.lorasupervisor.protocol.buffer.LoRaBuffer;

import java.io.Closeable;
import java.io.PrintStream;

public class LoRaNodePort implements Closeable {
	public static LoRaNodePort openNode(SerialPort port) {
		if (!port.isOpen() && !port.openPort()) {
			return null;
		}
		LoRaNodePort nodePort = new LoRaNodePort(port);

		port.setBaudRate(115200);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		return nodePort;
	}

	private final SerialPort port;
	private final PrintStream out;
	private final LoRaBuffer buffer;

	public LoRaNodePort(SerialPort serialPort) {
		this.port = serialPort;
		this.out = new PrintStream(serialPort.getOutputStream());
		this.buffer = new LoRaBuffer();
		serialPort.addDataListener(buffer.getListener());
	}

	public void send(CharSequence data) {
		out.println(data);
	}

	public SerialPort getPort() {
		return port;
	}

	public LoRaBuffer getBuffer() {
		return buffer;
	}

	@Override
	public void close() {
		this.port.closePort();
		this.out.close();
	}
}
