package eu.deyanix.lorasupervisor.protocol;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class LoRaNodeProvider {
	private final List<LoRaNode> nodes = new ArrayList<>();

	public List<LoRaNode> getNodes() {
		return nodes;
	}

	public static void detect() {
		List<SerialPort> ports = Arrays.stream(SerialPort.getCommPorts())
				.filter(port -> port.getVendorID() == 0x10C4)
				.filter(port -> port.getProductID() == 0xEA60)
				.toList();

		for (SerialPort port : ports) {
			if (!port.openPort()) {
				continue;
			}
			port.setBaudRate(115200);
			port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
			port.addDataListener(new SerialPortMessageListener() {
				@Override
				public int getListeningEvents() {
					return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
				}

				@Override
				public byte[] getMessageDelimiter() {
					return new byte[] { (byte) '=' };
				}

				@Override
				public boolean delimiterIndicatesEndOfMessage() {
					return true;
				}

				@Override
				public void serialEvent(SerialPortEvent event)
				{
					byte[] messageBytes = event.getReceivedData();
					String message = new String(messageBytes);

					System.out.println("Received data: " + message);
				}
			});

			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(() -> {
				PrintStream output = new PrintStream(port.getOutputStream());
				output.println("+ID?");
			}, 0, 3, TimeUnit.SECONDS);

			System.out.println("Sent ID?");
		}
	}
}
