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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class LoRaEventConnection extends LoRaConnection {
	private static final Command RX_DONE = CommandFactory.createSetterArgs("RX",
			new Argument("DONE"),
			new Argument(),
			new Argument(),
			new ExtensibleArgument());
	private static final Command RX_TIMEOUT = CommandFactory.createSetterArgs("RX",
			new Argument("TIMEOUT"));
	private static final Command RX_ERROR = CommandFactory.createSetterArgs("RX",
			new Argument("ERROR"));
	private static final Command TX_DONE = CommandFactory.createSetterArgs("TX",
			new Argument("DONE"));
	private static final Command TX_TIMEOUT = CommandFactory.createSetterArgs("TX",
			new Argument("TIMEOUT"));
	private static final Command TX_START= CommandFactory.createSetterArgs("TX",
			new Argument("START"),
			new ExtensibleArgument());
	private static final Command RX_START= CommandFactory.createSetterArgs("RX",
			new Argument("START"));

	private final Map<Command, BiConsumer<LoRaPort, CommandResult>> commandHandlers = new HashMap<>();

	public LoRaEventConnection() {
		commandHandlers.put(RX_DONE, (port, result) -> {
			int rssi = result.getArgument(1)
					.flatMap(ArgumentData::getInteger)
					.orElse(0);
			int snr = result.getArgument(2)
					.flatMap(ArgumentData::getInteger)
					.orElse(0);
			String eventData = result.getArgument(3)
					.flatMap(ArgumentData::getString)
					.orElse(null);

			port.invokeListener(listener -> listener.onRxDone(port, rssi, snr, eventData));
		});

		commandHandlers.put(RX_ERROR, (port, result) -> {
			port.invokeListener(listener -> listener.onRxError(port));
		});

		commandHandlers.put(RX_TIMEOUT, (port, result) -> {
			port.invokeListener(listener -> listener.onRxTimeout(port));
		});

		commandHandlers.put(TX_DONE, (port, result) -> {
			port.invokeListener(listener -> listener.onTxDone(port));
		});

		commandHandlers.put(TX_TIMEOUT, (port, result) -> {
			port.invokeListener(listener -> listener.onTxTimeout(port));
		});

		commandHandlers.put(TX_START, (port, result) -> {
			String eventData = result.getArgument(1)
					.flatMap(ArgumentData::getString)
					.orElse(null);

			port.invokeListener(listener -> listener.onTxStart(port, eventData));
		});

		commandHandlers.put(RX_START, (port, result) -> {
			port.invokeListener(listener -> listener.onRxStart(port));
		});
	}

	@Override
	public boolean onReceive(LoRaPort port, String data) {
		return receive(port, data, false);
	}

	@Override
	public void onClear(LoRaPort port, String data) {
		super.onClear(port, data);
		receive(port, data, true);
	}

	private boolean receive(LoRaPort port, String data, boolean force) {
		for (Map.Entry<Command, BiConsumer<LoRaPort, CommandResult>> entry : commandHandlers.entrySet()) {
			Command command = entry.getKey();
			BiConsumer<LoRaPort, CommandResult> handler = entry.getValue();

			CommandResult result = receive(command, data);
			if (result != null) {
				if (result.isComplete() || force) {
					handler.accept(port, result);
					requestedData = 0;
				}
				return true;
			}
		}
		return false;
	}
}
