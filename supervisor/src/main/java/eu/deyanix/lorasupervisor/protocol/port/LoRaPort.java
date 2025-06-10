package eu.deyanix.lorasupervisor.protocol.port;

import com.fazecast.jSerialComm.SerialPort;
import eu.deyanix.lorasupervisor.protocol.LoRaConnection;
import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.DataArgument;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaCommandConnection;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

	public LoRaPort(SerialPort serialPort) {
		this.serialPort = serialPort;
		this.sender = new LoRaPortSender(this);
		this.receiver = new LoRaPortReceiver(this);
	}

	public LoRaCommandConnection send(Command tx, Command rx) {
		LoRaCommandConnection connection = new LoRaCommandConnection(tx, rx);
		attachConnection(connection);

		return connection;
	}

	public LoRaCommandConnection sendGetter(String name) {
		return send(CommandFactory.createGetter(name), CommandFactory.createSetter(name, new DataArgument()));
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
		return Collections.unmodifiableList(connections);
	}

	protected void attachConnection(LoRaConnection connection) {
		connections.add(connection);
		connection.onInit(this); // TODO: Stack!
	}

	protected void detachConnection(LoRaConnection connection) {
		connections.remove(connection);
	}
}
