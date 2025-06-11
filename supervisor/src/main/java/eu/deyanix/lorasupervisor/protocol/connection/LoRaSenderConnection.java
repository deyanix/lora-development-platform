package eu.deyanix.lorasupervisor.protocol.connection;

import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

public abstract class LoRaSenderConnection extends LoRaConnection {
	public abstract void onSend(LoRaPort port);
}
