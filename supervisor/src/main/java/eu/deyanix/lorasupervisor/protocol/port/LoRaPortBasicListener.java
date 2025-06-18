package eu.deyanix.lorasupervisor.protocol.port;

public class LoRaPortBasicListener implements LoRaPortListener {
	@Override
	public void onDisconnect(LoRaPort port) {
	}

	@Override
	public void onSend(LoRaPort port, String data) {
	}

	@Override
	public void onReceive(LoRaPort port, String data) {
	}

	@Override
	public void onRxDone(LoRaPort port, int rssi, int snr, String data) {
	}

	@Override
	public void onRxTimeout(LoRaPort port) {
	}

	@Override
	public void onRxError(LoRaPort port) {
	}

	@Override
	public void onTxDone(LoRaPort port) {
	}

	@Override
	public void onTxTimeout(LoRaPort port) {
	}

	@Override
	public void onTxStart(LoRaPort port, String data) {
	}

	@Override
	public void onRxStart(LoRaPort port) {
	}
}
