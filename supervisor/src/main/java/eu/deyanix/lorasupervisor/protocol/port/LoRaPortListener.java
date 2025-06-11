package eu.deyanix.lorasupervisor.protocol.port;

public interface LoRaPortListener {
	void onSend(LoRaPort port, String data);
	void onReceive(LoRaPort port, String data);
}
