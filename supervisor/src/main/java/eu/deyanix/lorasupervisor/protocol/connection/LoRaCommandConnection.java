package eu.deyanix.lorasupervisor.protocol.connection;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.LoRaConnection;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;
import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.Command;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoRaCommandConnection extends LoRaConnection {
	private final Command txCommand;
	private final Command rxCommand;
	private final CompletableFuture<Command> result = new CompletableFuture<>();

	public LoRaCommandConnection(Command txCommand, Command rxCommand) {
		this.txCommand = txCommand;
		this.rxCommand = rxCommand;
	}

	@Override
	public void onInit(LoRaPort port) {
		if (txCommand == null) {
			return;
		}

		BufferWriter buffer = new BufferWriter();
		buffer.append('+');
		buffer.append(txCommand.getName());

		Iterator<Argument> args = txCommand.getArguments().iterator();
		if (args.hasNext())
			buffer.append('=');

		while (args.hasNext()) {
			args.next().write(buffer);

			if (args.hasNext())
				buffer.append(',');
		}

		if (txCommand.isQuery())
			buffer.append('?');

		port.getSender().send(buffer.getData());
	}

	@Override
	public boolean onReceiveData(LoRaPort port, String data) {
		if (rxCommand == null) {
			return false;
		}

		BufferReader reader = new BufferReader(data);
		String cmd = reader.untilEnd('=').orElse("");

		if (!cmd.equals(rxCommand.getName())) {
			return false;
		}

		for (Argument arg : rxCommand.getArguments()) {
			if (!arg.read(reader)) {
				return false;
			}

			if (reader.getOffset() > reader.getBuffer().length()) {
				requestedData = reader.getOffset();
				return true;
			}
		}
		requestedData = 0;
		result.complete(rxCommand);

		return true;
	}

	@Override
	public void onTimeout(LoRaPort port) {
		requestedData = 0;
	}

	public Optional<Command> get(long timeMs) {
		try {
			return Optional.ofNullable(result.get(timeMs, TimeUnit.MILLISECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			return Optional.empty();
		}
	}
}
