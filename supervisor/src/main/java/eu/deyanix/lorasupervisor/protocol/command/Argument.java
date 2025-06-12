package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;

import java.util.Optional;

public class Argument {
	protected final String value;

	public Argument(String value) {
		this.value = value;
	}

	public Argument(ArgumentData data) {
		this.value = data.getString().orElse(null);
	}

	public Argument() {
		this.value = null;
	}

	public Optional<ArgumentData> read(BufferReader buffer) {
		String dataValue = buffer.untilEnd(',').orElse(null);
		if (value != null && !value.equals(dataValue)) {
			return Optional.empty();
		}

		return Optional.of(new ArgumentData(dataValue));
	}

	public void write(BufferWriter buffer) {
		if (value != null) {
			buffer.append(value);
		}
	}
}
