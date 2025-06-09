package eu.deyanix.lorasupervisor.protocol.buffer;

public class LoRaBufferData {
	private final String data;

	public LoRaBufferData(String data) {
		this.data = data;
	}

	public byte toByte() {
		return Byte.parseByte(data);
	}

	public short toShort() {
		return Short.parseShort(data);
	}

	public int toInt() {
		return Integer.parseInt(data);
	}

	public long toLong() {
		return Long.parseLong(data);
	}

	public boolean toBoolean() {
		return toByte() != 0;
	}

	@Override
	public String toString() {
		return data;
	}
}
