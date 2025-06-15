package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.connection.LoRaConnection;
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
	}

	protected void receive() throws IOException {
		lock.lock();
		try {
			LoRaConnection connection = handleTimeout();
			BufferWriter localBuffer = new BufferWriter();
			InputStream in = port.getSerialPort().getInputStream();

			while (in.available() > 0) {
				char c = (char) in.read();
				localBuffer.append(c);

				if (connection != null) {
					buffer.append(c);

					int requestedData = connection.getRequestedData();
					if (requestedData > buffer.length())
						continue;

					if (!provideData(connection, buffer.getData().substring(0, requestedData))) {
						connection = null;
					}
				} else if (BufferWriter.isDelimiter(c)) {
					if (buffer.isEmpty())
						continue;

					connection = broadcastData(buffer.getData());
					if (connection != null)
						buffer.append(c);
				} else {
					buffer.append(c);
				}
			}

			for (LoRaPortListener listener : port.getListeners()) {
				listener.onReceive(port, localBuffer.getData());
			}
		} finally {
			setTimeout(Duration.ofMillis(50));
			lock.unlock();
		}
	}

	protected LoRaConnection handleTimeout() {
		LoRaConnection connection = getCapturingConnection();
		boolean expired = isExpired();
		setTimeout(null);

		if (expired) {
			if (connection != null) {
				connection.onClear(port, buffer.getData());
			}
			buffer.clearAll();
			return null;
		}

		return connection;
	}

	protected LoRaConnection getCapturingConnection() {
		return port.getConnections().stream()
				.filter(LoRaConnection::isCapturing)
				.findFirst()
				.orElse(null);
	}

	protected LoRaConnection broadcastData(String data) {
		for (LoRaConnection connection : port.getConnections()) {
			if (provideData(connection, data)) {
				return connection;
			}
		}
		return null;
	}

	protected boolean provideData(LoRaConnection connection, String data) {
		boolean received = connection.onReceive(port, data);
		if (!received)
			return false;

		if (!connection.isCapturing()) {
			if (data.length() == buffer.length()) {
				buffer.clearAll();
			} else {
				buffer.clear(data.length());
			}
			return false;
		}
		return true;
	}

	protected boolean isExpired() {
		if (expirationDate == null) {
			return false;
		}

		return expirationDate.isBefore(LocalDateTime.now());
	}

	protected void setTimeout(Duration timeout) {
		if (timeout == null) {
			expirationDate = null;
		} else {
			expirationDate = LocalDateTime.now().plus(timeout);
		}
	}
}
