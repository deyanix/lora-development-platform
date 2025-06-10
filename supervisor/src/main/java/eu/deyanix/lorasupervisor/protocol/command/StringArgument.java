package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.util.Objects;

public class StringArgument extends Argument {
	private boolean matching = false;

	public StringArgument(String value) {
		this.value = value;
	}

	public StringArgument() {
		this(null);
	}

	@Override
	public boolean read(BufferReader buffer) {
		String data = buffer.untilEnd(',').orElse(null);
		if (matching) {
			return Objects.equals(value, data);
		} else if (data != null) {
			value = data;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean write(BufferWriter buffer) {
		if (value == null) {
			return false;
		}

		buffer.append(value);
		return true;
	}
}
