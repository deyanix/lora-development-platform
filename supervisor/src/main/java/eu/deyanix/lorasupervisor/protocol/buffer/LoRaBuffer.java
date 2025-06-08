package eu.deyanix.lorasupervisor.protocol.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class LoRaBuffer {
	public static final char[] BUFFER_DELIMITERS = {'\n', '\r'};

	public static boolean isDelimiter(char character) {
		return IntStream.range(0, LoRaBuffer.BUFFER_DELIMITERS.length)
				.mapToObj(i -> LoRaBuffer.BUFFER_DELIMITERS[i])
				.anyMatch(c -> c == character);
	}

	private final List<LoRaBufferLine> lines = new ArrayList<>();

	public List<LoRaBufferLine> getLines() {
		return lines;
	}

	public LoRaBufferLine getCurrentLine() {
		if (lines.isEmpty()) {
			return null;
		}

		return lines.get(lines.size() - 1);
	}

	public void append(String text) {
		if (text.isEmpty()) {
			return;
		}

		LoRaBufferLine line = getCurrentLine();
		int index = 0;
		while (index < text.length()) {
			if (line == null) {
				line = new LoRaBufferLine();
				lines.add(line);
			}

			index += line.append(text.substring(index));
			line = null;
		}
	}
}
