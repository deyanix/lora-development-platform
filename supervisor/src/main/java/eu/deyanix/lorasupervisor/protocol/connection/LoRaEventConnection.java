package eu.deyanix.lorasupervisor.protocol.connection;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.ArgumentData;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.CommandResult;
import eu.deyanix.lorasupervisor.protocol.command.CommandTokenizer;
import eu.deyanix.lorasupervisor.protocol.command.ExtensibleArgument;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPortListener;

public class LoRaEventConnection extends LoRaConnection {
	private static final Command RX_DONE = CommandFactory.createSetterArgs("RX",
			new Argument("DONE"),
			new Argument(),
			new Argument(),
			new ExtensibleArgument());
	private static final Command TX_DONE = CommandFactory.createSetterArgs("TX",
			new Argument("DONE"),
			new ExtensibleArgument());

	@Override
	public boolean onReceive(LoRaPort port, String data) {
		BufferReader reader = new BufferReader(data);
		CommandResult commandResult = new CommandTokenizer(RX_DONE).read(reader);
		if (commandResult == null) {
			return false;
		}

		if (reader.getOffset() > reader.getBuffer().length()) {
			requestedData = reader.getOffset();
		} else {
			String value = commandResult
					.getArgument(3)
					.flatMap(ArgumentData::getString)
					.orElse(null);
			for (LoRaPortListener listener : port.getListeners()) {
				listener.onRxDone(port, value);
			}
		}

		return true;
	}
}
