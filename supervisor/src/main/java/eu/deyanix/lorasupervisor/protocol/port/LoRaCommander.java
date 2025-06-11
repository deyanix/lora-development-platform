package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.DataArgument;

import java.util.List;
import java.util.stream.IntStream;

public class LoRaCommander {
	private final LoRaPort port;

	public LoRaCommander(LoRaPort port) {
		this.port = port;
	}

	public List<Argument> sendDataGetter(String name, int args) {
		Command tx = CommandFactory.createGetter(name);
		Command rx = CommandFactory.createSetter(name, IntStream.range(0, args)
				.mapToObj(i -> new DataArgument())
				.toArray(DataArgument[]::new));

		return port.send(tx, rx)
				.map(Command::getArguments)
				.orElseThrow(); // TODO: Internal exception
	}

	public Argument sendDataGetter(String name) {
		Command tx = CommandFactory.createGetter(name);
		Command rx = CommandFactory.createSetter(name, new DataArgument());

		return port.send(tx, rx)
				.map(cmd -> cmd.getArgument(0))
				.orElseThrow();
	}

	public void sendDataSetter(String name, Argument... args) {
		Command tx = CommandFactory.createSetter(name, args);
		Command rx = CommandFactory.createSetter(name, new DataArgument());

		port.send(tx, rx)
				.map(cmd -> cmd.getArgument(0))
				.flatMap(Argument::getString)
				.filter(val -> val.equals("OK"))
				.orElseThrow();
	}

	public String getId() {
		return sendDataGetter("ID")
				.getString()
				.orElseThrow();
	}

	public long getFrequency() {
		return sendDataGetter("FRQ")
				.getLong()
				.orElseThrow();
	}

	public void setFrequency(long value) {
		sendDataSetter("FRQ",
				new DataArgument().setLong(value));
	}

	public LoRaPort getPort() {
		return port;
	}
}
