package eu.deyanix.lorasupervisor.protocol.connection;

import eu.deyanix.lorasupervisor.protocol.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.LoRaPortConnection;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.ExtensibleStringArgument;
import eu.deyanix.lorasupervisor.protocol.command.StringArgument;

public class LoRaCommandConnection extends LoRaPortConnection {
	private final boolean enableTx;

	public LoRaCommandConnection(boolean enableTx) {
		this.enableTx = enableTx;
	}

	@Override
	public void onInit(LoRaPort port) {
		port.send("+ID?");
		port.send("+MODE=" + (enableTx ? "TX" : "RX"));
//		port.send("+FRQ=869000000");
		port.send("+PUSH");

		if (enableTx) {
			//port.send("+TX=15,\nAAB\nXXYY\r\nGH\n\r");
			port.send("+TX=4,\nabc");
		}
	}

	@Override
	public boolean onReceiveData(LoRaPort port, String data) {
		System.out.println(port.getSerialPort().getSystemPortName() + " < (" + data.length() + ") " + data);

		Command command = new Command("RX")
				.append(new StringArgument("DONE"))
				.append(new StringArgument())
				.append(new StringArgument())
				.append(new ExtensibleStringArgument());

		BufferReader reader = new BufferReader(data);
		String cmd = reader.untilEnd('=').orElse("");

		if (!cmd.equals(command.getName())) {
			return false;
		}

		for (Argument arg : command.getArguments()) {
			if (!arg.read(reader)) {
				return false;
			}

			if (reader.getOffset() > reader.getBuffer().length()) {
				requestedData = reader.getOffset();
				return true;
			}
		}
		requestedData = 0;
		System.out.print(port.getSerialPort().getSystemPortName() + " - ");
		System.out.printf("RSSI=%d, SNR=%d, PAYLOAD=%d,%s",
				command.getArgument(1).getInteger().orElse(null),
				command.getArgument(2).getInteger().orElse(null),
				command.getArgument(3).getString().map(String::length).orElse(null),
				command.getArgument(3).getString().orElse(null));

		return true;
	}

	@Override
	public void onTimeout(LoRaPort port) {
		requestedData = 0;
	}
}
