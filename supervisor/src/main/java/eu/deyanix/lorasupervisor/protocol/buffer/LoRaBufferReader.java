package eu.deyanix.lorasupervisor.protocol.buffer;

public class LoRaBufferReader {
	private final LoRaBuffer buffer;
	private int currentLine = 0;

	public LoRaBufferReader(LoRaBuffer buffer) {
		this.buffer = buffer;
	}

//	public String get() {
//		return buffer.getLines().get(currentLine).getData().;
//	}
}
