package eu.deyanix.lorasupervisor.protocol.port;

import com.fazecast.jSerialComm.SerialPort;
import eu.deyanix.lorasupervisor.protocol.command.CommandResult;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaConnection;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaCommandConnection;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaSenderConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoRaPort {
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
	private final LoRaPortSender sender;
	private final LoRaPortReceiver receiver;
	private final List<LoRaConnection> connections = new ArrayList<>();
	private final List<LoRaPortListener> listeners = new ArrayList<>();
	private final Lock connectionLock = new ReentrantLock();
	private final Condition disconnect = connectionLock.newCondition();

	public LoRaPort(SerialPort serialPort) {
		this.serialPort = serialPort;
		this.sender = new LoRaPortSender(this);
		this.receiver = new LoRaPortReceiver(this);
	}

	public Optional<CommandResult> send(Command tx, Command rx, int retries) {
		for (int i = 0; i < retries; i++) {
			try {
				LoRaCommandConnection connection = new LoRaCommandConnection(tx, rx);
				attachConnection(connection);

				Optional<CommandResult> result = connection.get(500);
				detachConnection(connection);

				if (result.isPresent()) {
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Optional.empty();
	}

	public Optional<CommandResult> send(Command tx, Command rx) {
		return send(tx, rx, 1);
	}

	public LoRaCommander createCommander() {
		return new LoRaCommander(this);
	}

	public void addListener(LoRaPortListener listener) {
		listeners.add(listener);
	}

	public void removeListener(LoRaPortListener listener) {
		listeners.remove(listener);
	}

	public List<LoRaPortListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public LoRaPortSender getSender() {
		return sender;
	}

	public LoRaPortReceiver getReceiver() {
		return receiver;
	}

	public List<LoRaConnection> getConnections() {
		connectionLock.lock();
		try {
			return connections.stream().toList();
		} finally {
			connectionLock.unlock();
		}
	}

	protected void attachConnection(LoRaConnection connection) {
		connectionLock.lock();
		try {
			if (connection instanceof LoRaSenderConnection senderConnection) {
				while (connections.stream().anyMatch(LoRaSenderConnection.class::isInstance)) {
					disconnect.await();
				}

				connections.add(connection);
				senderConnection.onSend(this);
			} else {
				connections.add(connection);
			}
		} catch (InterruptedException e) {
			e.printStackTrace(); // TODO: Helper
		} finally {
			connectionLock.unlock();
		}
	}

	protected void detachConnection(LoRaConnection connection) {
		connectionLock.lock();
		try {
			if (connections.remove(connection)) {
				connection.onClose(this);
				disconnect.signal();
			}
		} finally {
			connectionLock.unlock();
		}
	}
}
