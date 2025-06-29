package eu.deyanix.lorasupervisor.protocol.event.rx;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaRxDoneEvent extends LoRaRxFinishEvent {
	private final int rssi;
	private final int snr;
	private final String data;

	public LoRaRxDoneEvent(LoRaPort port, int rssi, int snr, String data) {
		super("RX_DONE", true, port);
		this.rssi = rssi;
		this.snr = snr;
		this.data = data;
	}

	public int getRssi() {
		return rssi;
	}

	public int getSnr() {
		return snr;
	}

	public String getData() {
		return data;
	}
}
