package eu.deyanix.lorasupervisor.protocol.connection;


import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public abstract class LoRaConnection {
	protected int priority = 0;
	protected int requestedData = 0;

	public abstract boolean onReceive(LoRaPort port, String data);

	public void onClose(LoRaPort port) {
		requestedData = 0;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isCapturing() {
		return requestedData > 0;
	}

	public int getRequestedData() {
		return requestedData;
	}
}
