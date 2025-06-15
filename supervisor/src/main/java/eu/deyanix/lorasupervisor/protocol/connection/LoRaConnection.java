package eu.deyanix.lorasupervisor.protocol.connection;


import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandResult;
import eu.deyanix.lorasupervisor.protocol.command.CommandTokenizer;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public abstract class LoRaConnection {
	protected int priority = 0;
	protected int requestedData = 0;

	public abstract boolean onReceive(LoRaPort port, String data);

	public void onClear(LoRaPort port, String data) {
		requestedData = 0;
	}

	public void onClose(LoRaPort port) {
		requestedData = 0;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isCapturing() {
		return requestedData > 0;
	}

	public int getRequestedData() {
		return requestedData;
	}

	protected CommandResult receive(CommandTokenizer command, String data) {
		BufferReader reader = new BufferReader(data);
		CommandResult commandResult = command.read(reader);
		if (commandResult == null) {
			return null;
		}

		if (!commandResult.isComplete()) {
			requestedData = reader.getOffset();
		}
		return commandResult;
	}

	protected CommandResult receive(Command command, String data) {
		return receive(new CommandTokenizer(command), data);
	}
}
