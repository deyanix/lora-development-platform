package eu.deyanix.lorasupervisor.protocol.connection;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public class LoRaEventConnection extends LoRaConnection {
	@Override
	public boolean onReceive(LoRaPort port, String data) {
		return false;
	}

	@Override
	public void onClose(LoRaPort port) {
		requestedData = 0;
	}
}
