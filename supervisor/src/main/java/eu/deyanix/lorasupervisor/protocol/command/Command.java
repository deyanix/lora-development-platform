package eu.deyanix.lorasupervisor.protocol.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Command {
	private final String name;
	private final boolean query;
	private final List<Argument> arguments;

	public Command(String name, boolean query, Argument... arguments) {
		this.name = name;
		this.query = query;
		this.arguments = List.of(arguments);
	}

	public String getName() {
		return name;
	}

	public boolean isQuery() {
		return query;
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	public Argument getArgument(int index) {
		return arguments.get(index);
	}

	public boolean hasArguments() {
		return !arguments.isEmpty();
	}
}
