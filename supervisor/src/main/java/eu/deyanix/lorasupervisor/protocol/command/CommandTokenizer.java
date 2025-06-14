package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CommandTokenizer {
	private final Command command;

	public CommandTokenizer(Command command) {
		this.command = command;
	}

	public void write(BufferWriter buffer) {
		buffer.append('+');
		buffer.append(command.getName());

		Iterator<Argument> args = command.getArguments().iterator();
		if (args.hasNext())
			buffer.append('=');

		while (args.hasNext()) {
			args.next().write(buffer);

			if (args.hasNext())
				buffer.append(',');
		}

		if (command.isQuery())
			buffer.append('?');

		buffer.append('\n');
	}

	public CommandResult read(BufferReader buffer) {
		String cmd = buffer.untilEnd('=').orElse("");

		if (!cmd.equals(command.getName())) {
			return null;
		}

		boolean complete = true;
		List<ArgumentData> argumentsData = new ArrayList<>();
		for (Argument arg : command.getArguments()) {
			Optional<ArgumentData> argumentData = arg.read(buffer);
			if (argumentData.isEmpty()) {
				return null;
			}

			if (buffer.getOffset() > buffer.getBuffer().length()) {
				complete = false;
				break;
			}

			argumentsData.add(argumentData.get());
		}

		return new CommandResult(command, argumentsData, complete, buffer.getOffset());
	}
}
