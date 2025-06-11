package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;

import java.util.Optional;

public abstract class Argument implements Cloneable {
	protected String value;

	public abstract boolean read(BufferReader buffer);
	public abstract boolean write(BufferWriter buffer);

	public Optional<String> getString() {
		return Optional.ofNullable(value);
	}

	public Argument setString(String value) {
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

	public Argument setInteger(Integer value) {
		if (value != null) {
			this.value = value.toString();
		} else {
			this.value = null;
		}
		return this;
	}

	public Optional<Long> getLong() {
		try {
			return Optional.of(Long.parseLong(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public Argument setLong(Long value) {
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

	public Argument setBoolean(Boolean value) {
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

	@Override
	public Argument clone() {
		try {
			return (Argument) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
