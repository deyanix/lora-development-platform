package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.util.Optional;

public class ExtensibleStringArgument implements Argument {
	private String value = null;

	public ExtensibleStringArgument() {
	}

	@Override
	public boolean read(BufferReader buffer) {
		String data = buffer.until(',').orElse(null);
		if (data != null) {
			try {
				int size = Integer.parseInt(data);
				value = buffer.take(size).orElse(null);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean write(BufferWriter buffer) {
		if (value == null) {
			return false;
		}

		buffer.append(String.valueOf(value.length()));
		buffer.append(',');
		buffer.append(value);
		return true;
	}

	public Optional<String> getString() {
		return Optional.ofNullable(value);
	}
}
