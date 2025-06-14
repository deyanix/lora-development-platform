package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;

public interface LoRaPortListener {
	void onDisconnect(LoRaPort port);
	void onSend(LoRaPort port, String data);
	void onReceive(LoRaPort port, String data);
	void onRxDone(LoRaPort port, int rssi, int snr, String data);
	void onRxTimeout(LoRaPort port);
	void onRxError(LoRaPort port);
	void onTxDone(LoRaPort port, String data);
	void onTxTimeout(LoRaPort port);
}
