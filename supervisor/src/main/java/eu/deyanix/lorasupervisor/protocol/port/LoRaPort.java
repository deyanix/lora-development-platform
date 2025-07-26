package eu.deyanix.lorasupervisor.protocol.port;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.command.CommandResult;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaConnection;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaCommandConnection;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaEventConnection;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaSenderConnection;
import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.event.LoRaNodeEvent;
import eu.deyanix.lorasupervisor.protocol.event.port.LoRaPortConnectEvent;
import eu.deyanix.lorasupervisor.protocol.event.port.LoRaPortDisconnectEvent;
import eu.deyanix.lorasupervisor.protocol.event.port.LoRaPortRecognizedEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LoRaPort {
	private final SerialPort serialPort;
	private final LoRaPortSender sender;
	private final LoRaPortReceiver receiver;
	private final List<LoRaConnection> connections = new ArrayList<>();
	private final List<LoRaPortListener> listeners = new ArrayList<>();
	private final Lock connectionLock = new ReentrantLock();
	private final Condition disconnect = connectionLock.newCondition();
	private LoRaNode node;

	public LoRaPort(SerialPort serialPort) {
		this.serialPort = serialPort;
		this.sender = new LoRaPortSender(this);
		this.receiver = new LoRaPortReceiver(this);

		this.serialPort.setBaudRate(115200);
		this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		this.serialPort.addDataListener(new LoRaSerialPortListener());

		attachConnection(new LoRaEventConnection());
	}

	public boolean open() {
		if (serialPort.isOpen()) {
			return true;
		}

		if (!serialPort.openPort()) {
			return false;
		}

		invokeEvent(new LoRaPortConnectEvent(this));
		return true;
	}

	public void close() {
		serialPort.closePort();
		if (node != null) {
			node.setCommander(null);
		}

		invokeEvent(new LoRaPortDisconnectEvent(this));
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

	public void invokeEvent(LoRaEvent event) {
		for (LoRaPortListener listener : getListeners()) {
			listener.onEvent(event);
		}

		if (event instanceof LoRaNodeEvent nodeEvent) {
			LoRaNode node = nodeEvent.getNode();
			if (node != null) {
				node.getEvents().add(nodeEvent);
			}
		}
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

	public LoRaNode getNode() {
		return node;
	}

	public LoRaPort setNode(LoRaNode node) {
		this.node = node;
		invokeEvent(new LoRaPortRecognizedEvent(this));

		return this;
	}

	protected class LoRaSerialPortListener implements SerialPortDataListener {
		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_DATA_AVAILABLE | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
				invokeEvent(new LoRaPortDisconnectEvent(LoRaPort.this));
			}

			if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
				try {
					receiver.receive();
				} catch (Exception e) {
					e.printStackTrace(); // TODO: Helper
				}
			}
		}
	}
}
