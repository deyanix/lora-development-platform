package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;

import java.util.Optional;

public class StringArgument implements Argument {
	private String value;

	public StringArgument(String value) {
		this.value = value;
	}

	public StringArgument() {
		this(null);
	}

	@Override
	public boolean read(BufferReader buffer) {
		String data = buffer.untilEnd(',').orElse(null);
		if (value != null) {
			return value.equals(data);
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

	public Optional<String> getString() {
		return Optional.ofNullable(value);
	}

	public StringArgument setString(String value) {
		this.value = value;
		return this;
	}

	public Optional<Integer> getInteger() {
		try {
			return Optional.of(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public StringArgument setInteger(Integer value) {
		if (value != null) {
			this.value = value.toString();
		} else {
			this.value = null;
		}
		return this;
	}

	public Optional<Boolean> getBoolean() {
		if ("1".equals(value)) {
			return Optional.of(true);
		} else if ("0".equals(value)) {
			return Optional.of(false);
		} else {
			return Optional.empty();
		}
	}

	public StringArgument setBoolean(Boolean value) {
		if (value != null) {
			this.value = value ? "1" : "0";
		} else {
			this.value = null;
		}
		return this;
	}

	public boolean hasValue() {
		return value != null;
	}
}
