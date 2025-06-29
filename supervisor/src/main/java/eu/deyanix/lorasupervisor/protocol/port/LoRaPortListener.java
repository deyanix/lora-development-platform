package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;

public interface LoRaPortListener {
	void onEvent(LoRaEvent event);
}
