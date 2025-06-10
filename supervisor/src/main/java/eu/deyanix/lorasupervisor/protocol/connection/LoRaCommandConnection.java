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
		port.send("+MODE=" + (enableTx ? "TX" : "RX"));
		port.send("+PUSH");
		if (enableTx) {
			//port.send("+TX=15,\nAAB\nXXYY\r\nGH\n\r");
			port.send("+TX=3,\nabc");
		}
	}

	@Override
	public boolean onReceiveData(LoRaPort port, String data) {
		System.out.println(port.getSerialPort().getSystemPortName() + "] DATA = " + data.length() + ',' + data);
		StringArgument rssiArg = new StringArgument();
		StringArgument snrArg = new StringArgument();
		ExtensibleStringArgument payloadArg = new ExtensibleStringArgument();

		Command command = new Command("RX")
				.append(new StringArgument("DONE"))
				.append(rssiArg)
				.append(snrArg)
				.append(payloadArg);


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
		System.out.println(port.getSerialPort().getSystemPortName() + "] MODE = DONE"); // TODO: too many!
//		System.out.println("RSSI = " + rssiArg.getInteger().orElse(null));
//		System.out.println("SNR = " + rssiArg.getInteger().orElse(null));
//		System.out.println("PAYLOAD = " + payloadArg.getString().orElse(null));

		return true;
	}

	@Override
	public void onTimeout(LoRaPort port) {
		requestedData = 0;
	}
}
