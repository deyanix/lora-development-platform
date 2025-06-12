package eu.deyanix.lorasupervisor.protocol.connection;

import eu.deyanix.lorasupervisor.protocol.command.ArgumentData;
import eu.deyanix.lorasupervisor.protocol.command.CommandResult;
import eu.deyanix.lorasupervisor.protocol.command.CommandTokenizer;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;
import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.Command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoRaCommandConnection extends LoRaSenderConnection {
	private final CommandTokenizer txCommand;
	private final CommandTokenizer rxCommand;
	private final CompletableFuture<CommandResult> result = new CompletableFuture<>();

	public LoRaCommandConnection(Command txCommand, Command rxCommand) {
		this.txCommand = txCommand != null ?
				new CommandTokenizer(txCommand) : null;
		this.rxCommand = rxCommand != null ?
				new CommandTokenizer(rxCommand) : null;
	}

	@Override
	public void onSend(LoRaPort port) {
		if (txCommand == null) {
			return;
		}

		BufferWriter buffer = new BufferWriter();
		txCommand.write(buffer);

		port.getSender().send(buffer.getData());
	}

	@Override
	public boolean onReceive(LoRaPort port, String data) {
		if (rxCommand == null) {
			return false;
		}

		BufferReader reader = new BufferReader(data);
		CommandResult commandResult = rxCommand.read(reader);
		if (commandResult == null) {
			return false;
		}

		if (reader.getOffset() > reader.getBuffer().length()) {
			requestedData = reader.getOffset();
		} else {
			result.complete(commandResult);
		}
		return true;
	}

	public Optional<CommandResult> get(long timeMs) {
		try {
			return Optional.ofNullable(result.get(timeMs, TimeUnit.MILLISECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			return Optional.empty();
		}
	}
}
