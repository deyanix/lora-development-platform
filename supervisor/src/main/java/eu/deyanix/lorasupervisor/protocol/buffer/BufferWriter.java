package eu.deyanix.lorasupervisor.protocol.buffer;

import java.util.Set;

public class BufferWriter {
	public static final Set<Character> BUFFER_DELIMITERS = Set.of('\n', '\r');

	public static boolean isDelimiter(char character) {
		return BUFFER_DELIMITERS.contains(character);
	}

	public static boolean hasDelimiter(CharSequence text) {
		return text.chars().anyMatch(c -> isDelimiter((char) c));
	}

	private final StringBuffer buffer = new StringBuffer();

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

	public void clear(int len) {
		buffer.delete(0, len);
	}
}
