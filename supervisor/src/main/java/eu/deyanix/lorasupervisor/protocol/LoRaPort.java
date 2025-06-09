package eu.deyanix.lorasupervisor.protocol;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoRaPort implements Closeable {
	public static LoRaPort openNode(SerialPort port) {
		if (!port.isOpen() && !port.openPort()) {
			return null;
		}
		LoRaPort nodePort = new LoRaPort(port);

		port.setBaudRate(115200);
		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		return nodePort;
	}

	private final SerialPort serialPort;
	private final PrintStream out;
	private final Lock readLock = new ReentrantLock();
	private final Lock writeLock = new ReentrantLock();
	private final BufferWriter buffer = new BufferWriter();
	private int requestedData = 0;

	private final LoRaBufferListener listener = new LoRaBufferListener();
	private final List<LoRaPortConnection> connections = new ArrayList<>();

	public LoRaPort(SerialPort serialPort) {
		this.serialPort = serialPort;
		this.out = new PrintStream(serialPort.getOutputStream());
		serialPort.addDataListener(listener);
	}

	public void send(CharSequence data) {
		writeLock.lock();
		try {
			System.out.println(serialPort.getSystemPortName() + " > " + data);
			out.println(data);
		} finally {
			writeLock.unlock();
		}
	}

	public void attachConnection(LoRaPortConnection connection) {
		connections.add(connection);
		connection.onInit(this);
	}

	public void detachConnection(LoRaPortConnection connection) {
		connections.remove(connection);
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	@Override
	public void close() {
		this.serialPort.closePort();
		this.out.close();
	}

	public int getRequestedData() {
		return requestedData;
	}

	public void setRequestedData(int requestedData) {
		this.requestedData = requestedData;
	}

	protected void receive(String data) {
		readLock.lock();
		try {
			LoRaPortConnection connection = getCapturingConnection();

			if (buffer.isExpired()) {
				buffer.clearAll();
				if (connection != null) {
					connection.onTimeout(this);
				}
			}


			int offset = 0;
			while (offset < data.length()) {
				char c = data.charAt(offset++);

				if (connection != null) {
					buffer.append(c);

					if (connection.getRequestedData() <= buffer.length()) {
						connection.onReceiveData(this, buffer.getData());
					}
				} else if (!BufferWriter.isDelimiter(c)) {
					buffer.append(c);
				} else if (!buffer.isEmpty()) {
					connection = broadcastData(buffer.getData());

					if (connection != null) {
						buffer.append(c);
					} else {
						buffer.clearAll();
					}
				}
			}

			buffer.setTimeout(Duration.ofMillis(5)); // TODO: Hardcoded timeout
		} finally {
			readLock.unlock();
		}
	}

	protected LoRaPortConnection getCapturingConnection() {
		return connections.stream()
				.filter(LoRaPortConnection::isCapturing)
				.findFirst()
				.orElse(null);
	}

	protected LoRaPortConnection broadcastData(String data) {
		for (LoRaPortConnection connection : connections) {
			connection.onReceiveData(this, data);
			if (connection.isCapturing()) {
				return connection;
			}
		}
		return null;
	}

	protected class LoRaBufferListener implements SerialPortDataListener {
		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
				try (InputStream in = event.getSerialPort().getInputStream()) {
					StringBuilder sb = new StringBuilder();
					while (in.available() > 0) {
						sb.append((char) in.read());
					}

					receive(sb.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
