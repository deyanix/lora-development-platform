package eu.deyanix.lorasupervisor.protocol.command;

import java.util.Optional;

public class ArgumentData {
	private String value;

	public ArgumentData(String value) {
		this.value = value;
	}

	public ArgumentData() {
		this(null);
	}

	public String getValue() {
		return value;
	}

	public Optional<String> getString() {
		return Optional.ofNullable(value);
	}

	public ArgumentData setString(String value) {
		this.value = value;
		return new ArgumentData(value);
	}

	public Optional<Integer> getInteger() {
		try {
			return Optional.of(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public ArgumentData setInteger(Integer value) {
		if (value != null) {
			return setString(value.toString());
		} else {
			return setString(null);
		}
	}

	public Optional<Long> getLong() {
		try {
			return Optional.of(Long.parseLong(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public ArgumentData setLong(Long value) {
		if (value != null) {
			return setString(value.toString());
		} else {
			return setString(null);
		}
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

	public ArgumentData setBoolean(Boolean value) {
		if (value != null) {
			return setString(value ? "1" : "0");
		} else {
			return setString(null);
		}
	}

	public boolean hasValue() {
		return value != null;
	}
}
