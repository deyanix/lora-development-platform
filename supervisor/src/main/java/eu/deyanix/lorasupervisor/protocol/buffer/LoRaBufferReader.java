package eu.deyanix.lorasupervisor.protocol.buffer;

import java.util.stream.IntStream;

public class LoRaBufferReader {
	private final String buffer;
	private int offset;
	private int request = -1;

	public LoRaBufferReader(String buffer) {
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

	public LoRaBufferData until(char delimiter) {
		return untilTo(buffer.indexOf(delimiter, offset) + 1);
	}

	public LoRaBufferData untilEndLine() {
		int index = IntStream.range(offset, buffer.length())
				.filter(i -> LoRaBuffer.isDelimiter(buffer.charAt(i)))
				.findFirst()
				.orElse(buffer.length());

		return untilTo(index);
	}

	public LoRaBufferData untilEnd() {
		return untilTo(buffer.length());
	}

	public LoRaBufferData untilTo(int newOffset) {
		if (newOffset < offset) {
			return null;
		}

		int from = offset;
		offset = newOffset;
		return new LoRaBufferData(buffer.substring(from, offset));
	}

	public void skip(int len) {
		if (offset+len <= buffer.length()) {
			offset += len;
		} else {
			offset = buffer.length();
		}
	}

	public void request(int request) {
		this.request = request;
	}

	public void complete() {
		this.request = 0;
	}

	public String getBuffer() {
		return buffer;
	}

	public int getOffset() {
		return offset;
	}

	public int getRequest() {
		return request;
	}
}
