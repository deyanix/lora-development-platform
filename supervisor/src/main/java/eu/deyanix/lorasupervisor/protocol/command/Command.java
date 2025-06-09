package eu.deyanix.lorasupervisor.protocol.command;

import java.util.ArrayList;
import java.util.List;

public class Command {
	private final String name;
	private final List<Argument> arguments = new ArrayList<Argument>();

	public Command(String name) {
		this.name = name;
	}

	public Command append(Argument argument) {
		this.arguments.add(argument);
		return this;
	}

	public String getName() {
		return name;
	}

	public Argument[] getArguments() {
		return arguments.toArray(new Argument[0]);
	}
}
