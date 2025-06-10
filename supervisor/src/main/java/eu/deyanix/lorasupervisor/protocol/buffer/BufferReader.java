package eu.deyanix.lorasupervisor.protocol.buffer;

import java.util.Optional;

public class BufferReader {
	private final String buffer;
	private int offset;

	public BufferReader(String buffer) {
		this.buffer = buffer;
	}

	public boolean with(char c) {
		if (offset+1 > buffer.length()) {
			return false;
		}

		if (buffer.charAt(offset) == c) {
			offset++;
			return true;
		}
		return false;
	}

	public boolean with(String str) {
		if (offset+str.length() > buffer.length()) {
			return false;
		}

		if (buffer.startsWith(str, offset)) {
			offset += str.length();
			return true;
		}
		return false;
	}

	public Optional<String> take(int length) {
		int index = offset+length;
		int newOffset = Math.min(index, buffer.length());
		return untilTo(newOffset, index-newOffset);
	}

	public Optional<String> untilEnd(char delimiter) {
		return until(delimiter).or(this::untilEnd);
	}

	public Optional<String> untilEnd() {
		return untilTo(buffer.length(), 0);
	}

	public Optional<String> until(char delimiter) {
		return untilTo(buffer.indexOf(delimiter, offset), 1);
	}

	public Optional<String> untilTo(int newOffset, int skip) {
		if (newOffset < offset) {
			return Optional.empty();
		}

		int oldOffset = offset;
		offset = newOffset+skip;
		if (oldOffset > buffer.length() || newOffset > buffer.length()) {
			return Optional.empty();
		}

		return Optional.of(buffer.substring(oldOffset, newOffset));
	}

	public String getBuffer() {
		return buffer;
	}

	public int getOffset() {
		return offset;
	}

	public boolean isComplete() {
		return offset >= buffer.length();
	}
}
