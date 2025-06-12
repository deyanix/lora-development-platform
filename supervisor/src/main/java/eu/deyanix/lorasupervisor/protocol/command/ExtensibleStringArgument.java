package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.util.Optional;

public class ExtensibleStringArgument extends Argument {
	public ExtensibleStringArgument(String value) {
		super(value);
	}

	public ExtensibleStringArgument(ArgumentData data) {
		super(data);
	}

	public ExtensibleStringArgument() {
	}

	@Override
	public Optional<ArgumentData> read(BufferReader buffer) {
		String data = buffer.until(',').orElse(null);
		if (data != null) {
			try {
				int size = Integer.parseInt(data);
				String dataValue = buffer.take(size).orElse(null);
				return Optional.of(new ArgumentData(dataValue));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void write(BufferWriter buffer) {
		if (value == null) {
			buffer.append(String.valueOf(0));
			buffer.append(',');
		} else {
			buffer.append(String.valueOf(value.length()));
			buffer.append(',');
			buffer.append(value);
		}
	}

	public Optional<String> getString() {
		return Optional.ofNullable(value);
	}
}
