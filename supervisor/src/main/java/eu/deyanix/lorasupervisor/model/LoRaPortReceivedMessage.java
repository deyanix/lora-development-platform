package eu.deyanix.lorasupervisor.model;

public class LoRaPortReceivedMessage extends LoRaPortMessage {
	private final int rssi;
	private final int snr;

	public LoRaPortReceivedMessage(String port, int rssi, int snr, String message) {
		super(port, message);
		this.rssi = rssi;
		this.snr = snr;
	}

	public int getRssi() {
		return rssi;
	}

	public int getSnr() {
		return snr;
	}
}
