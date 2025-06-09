package eu.deyanix.lorasupervisor.protocol.buffer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class BufferWriter {
	public static final char[] BUFFER_DELIMITERS = {'\n', '\r'};

	public static boolean isDelimiter(char character) {
		return IntStream.range(0, BUFFER_DELIMITERS.length)
				.mapToObj(i -> BUFFER_DELIMITERS[i])
				.anyMatch(c -> c == character);
	}

	public static boolean hasDelimiter(CharSequence text) {
		return text.chars().anyMatch(c -> isDelimiter((char) c));
	}

	private final StringBuffer buffer = new StringBuffer();
	private LocalDateTime expirationDate = null;

	public void append(char character) {
		buffer.append(character);
	}

	public void append(CharSequence text) {
		buffer.append(text);
	}

	public boolean isEmpty() {
		return buffer.isEmpty();
	}

	public int length() {
		return buffer.length();
	}

	public String getData() {
		return buffer.toString();
	}

	public void clearAll() {
		buffer.setLength(0);
	}

	public boolean isExpired() {
		if (expirationDate == null) {
			return false;
		}

		return expirationDate.isBefore(LocalDateTime.now());
	}

	public void setTimeout(Duration timeout) {
		if (timeout == null) {
			this.expirationDate = null;
		} else {
			this.expirationDate = LocalDateTime.now().plus(timeout);
		}
	}
}
