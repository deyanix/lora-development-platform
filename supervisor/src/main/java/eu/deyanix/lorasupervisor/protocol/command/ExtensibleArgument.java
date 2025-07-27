package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ExtensibleArgument extends Argument {
	public ExtensibleArgument(String value) {
		super(value);
	}

	public ExtensibleArgument(ArgumentData data) {
		super(data);
	}

	public ExtensibleArgument() {
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
			buffer.append(String.valueOf(value.getBytes(StandardCharsets.UTF_8).length));
			buffer.append(',');
			buffer.append(value);
		}
	}

	public Optional<String> getString() {
		return Optional.ofNullable(value);
	}
}
