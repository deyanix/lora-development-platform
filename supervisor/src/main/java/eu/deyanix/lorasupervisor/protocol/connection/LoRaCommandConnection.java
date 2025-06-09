package eu.deyanix.lorasupervisor.protocol.connection;

import eu.deyanix.lorasupervisor.protocol.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.LoRaPortConnection;
import eu.deyanix.lorasupervisor.protocol.buffer.LoRaBuffer;
import eu.deyanix.lorasupervisor.protocol.buffer.LoRaBufferReader;

public class LoRaCommandConnection extends LoRaPortConnection {
	private final boolean enableTx;

	public LoRaCommandConnection(boolean enableTx) {
		this.enableTx = enableTx;
	}

	@Override
	public void onInit(LoRaPort port) {
		port.send("+MODE=" + (enableTx ? "TX" : "RX"));
		port.send("+PUSH");
		if (enableTx) {
			port.send("+TX=13,AB\nXXYY\r\nGH\n\r");
		}
	}

	@Override
	public boolean onReceiveData(LoRaPort port, String data) {
		LoRaBufferReader reader = new LoRaBufferReader(data);
		if (reader.with("RX")) {
			if (reader.with("DONE,")) {
				var rssi = reader.until(',').toInt();
//				System.out.println("RSSI = " + rssi);

				var snr = reader.until(',').toInt();
//				System.out.println("SNR = " + snr);

				var size = reader.until(',').toInt();
//				System.out.println("Size = " + size);

				String payload = reader.untilEnd().toString();
				if (payload.length() < size) {
					requestedData = data.length() + (size - payload.length());
				} else {
					System.out.println("Payload = " + payload);
				}
			}
		}


		return true;
	}
}
