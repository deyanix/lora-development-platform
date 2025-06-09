package eu.deyanix.lorasupervisor.protocol;


public abstract class LoRaPortConnection {
	protected int priority = 0;
	protected int requestedData = 0;

	public abstract void onInit(LoRaPort port);
	public abstract boolean onReceiveData(LoRaPort port, String data);
	public abstract void onTimeout(LoRaPort port);

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
