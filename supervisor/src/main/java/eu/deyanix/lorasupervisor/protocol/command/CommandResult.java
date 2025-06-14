package eu.deyanix.lorasupervisor.protocol.command;

import java.util.List;
import java.util.Optional;

public class CommandResult {
	private final Command command;
	private final List<ArgumentData> arguments;
	private final boolean complete;
	private final int offset;

	public CommandResult(Command command, List<ArgumentData> arguments, boolean complete, int offset) {
		this.command = command;
		this.arguments = arguments;
		this.complete = complete;
		this.offset = offset;
	}

	public Command getCommand() {
		return command;
	}

	public List<ArgumentData> getArguments() {
		return arguments;
	}

	public Optional<ArgumentData> getArgument(int index) {
		if (index < 0 || index >= arguments.size()) {
			return Optional.empty();
		}
		return Optional.of(arguments.get(index));
	}

	public boolean isComplete() {
		return complete;
	}

	public int getOffset() {
		return offset;
	}
}
