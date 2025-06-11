package eu.deyanix.lorasupervisor.protocol.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Command {
	private final String name;
	private final boolean query;
	private final List<Argument> arguments = new ArrayList<Argument>();

	public Command(String name, boolean query) {
		this.name = name;
		this.query = query;
	}

	public Command(String name) {
		this(name, false);
	}

	public Command append(Argument argument) {
		this.arguments.add(argument);
		return this;
	}

	public Command append(Argument... arguments) {
		this.arguments.addAll(List.of(arguments));
		return this;
	}

	public String getName() {
		return name;
	}

	public boolean isQuery() {
		return query;
	}

	public List<Argument> getArguments() {
		return Collections.unmodifiableList(arguments);
	}

	public Argument getArgument(int index) {
		return arguments.get(index);
	}

	public boolean hasArguments() {
		return !arguments.isEmpty();
	}
}
