package eu.deyanix.lorasupervisor.protocol.buffer;

import java.time.LocalDateTime;

public class LoRaBufferLine {
	private final StringBuffer data = new StringBuffer();
	private LocalDateTime time = LocalDateTime.now();

	public StringBuffer getData() {
		return data;
	}

	public int getLength() {
		return data.length();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public LocalDateTime getTime() {
		return time;
	}

	public int append(String text) {
		boolean complete = isComplete();
		int index = 0;

		while (index < text.length()) {
			char c = text.charAt(index);
			boolean delimiter = LoRaBuffer.isDelimiter(c);
			if (complete && !delimiter) {
				break;
			}

			data.append(c);
			complete = delimiter;
			index++;
		}

		if (index > 0) {
			time = LocalDateTime.now();
		}

		return index;
	}

	public void delete(int index) {
		if (index < data.length()) {
			data.delete(0, index);
		} else {
			data.setLength(0);
		}
	}

	public boolean isComplete() {
		if (isEmpty()) {
			return false;
		}

		char lastChar = data.charAt(data.length() - 1);
		return LoRaBuffer.isDelimiter(lastChar);
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
