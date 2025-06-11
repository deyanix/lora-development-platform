package eu.deyanix.lorasupervisor.protocol.port;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import eu.deyanix.lorasupervisor.protocol.LoRaConnection;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoRaPortReceiver {
	private final LoRaPort port;
	private final Lock lock = new ReentrantLock();
	private final BufferWriter buffer = new BufferWriter();
	private LocalDateTime expirationDate = null;

	public LoRaPortReceiver(LoRaPort port) {
		this.port = port;
		this.port.getSerialPort().addDataListener(new LoRaReceiverListener());
	}

	protected void receive() throws IOException {
		lock.lock();
		try {
			handleTimeout();

			LoRaConnection connection = getCapturingConnection();

			InputStream in = port.getSerialPort().getInputStream();
			while (in.available() > 0) {
				char c = (char) in.read();

				if (connection != null && connection.isCapturing()) {
					buffer.append(c);

					int requestedData = connection.getRequestedData();
					if (requestedData <= buffer.length()) {
						connection.onReceiveData(port, buffer.getData().substring(0, requestedData));
					}

					if (!connection.isCapturing()) {
						connection = null;
						buffer.clear(requestedData);
					}
				} else if (BufferWriter.isDelimiter(c)) {
					if (buffer.isEmpty())
						continue;

					connection = broadcastData(buffer.getData());

					if (connection != null && connection.isCapturing()) {
						buffer.append(c);
					} else {
						buffer.clearAll();
					}
				} else {
					buffer.append(c);
				}
			}

			setTimeout(Duration.ofMillis(5)); // TODO: Hardcoded timeout
		} finally {
			lock.unlock();
		}
	}

	protected void handleTimeout() {
		LoRaConnection connection = getCapturingConnection();

		if (isExpired()) {
			buffer.clearAll();
			if (connection != null) {
				connection.onTimeout(port);
			}
		}
	}

	protected LoRaConnection getCapturingConnection() {
		return port.getConnections().stream()
				.filter(LoRaConnection::isCapturing)
				.findFirst()
				.orElse(null);
	}

	protected LoRaConnection broadcastData(String data) {
		for (LoRaConnection connection : port.getConnections()) {
			connection.onReceiveData(port, data);
			if (connection.isCapturing()) {
				return connection;
			}
		}
		return null;
	}

	protected boolean isExpired() {
		if (expirationDate == null) {
			return false;
		}

		return expirationDate.isBefore(LocalDateTime.now());
	}

	protected void setTimeout(Duration timeout) {
		if (timeout == null) {
			this.expirationDate = null;
		} else {
			this.expirationDate = LocalDateTime.now().plus(timeout);
		}
	}

	protected class LoRaReceiverListener implements SerialPortDataListener {
		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
				try {
					receive();
				} catch (IOException e) {
					e.printStackTrace(); // TODO: Helper
				}
			}
		}
	}
}
