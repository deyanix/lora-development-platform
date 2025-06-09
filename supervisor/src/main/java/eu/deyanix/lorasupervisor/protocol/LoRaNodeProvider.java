package eu.deyanix.lorasupervisor.protocol;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class LoRaNodeProvider {
	private static final int NODE_VENDOR_ID = 0x10C4;
	private static final int NODE_PRODUCT_ID = 0xEA60;
	private final ArrayList<LoRaNode> nodes = new ArrayList<>();

	public List<LoRaNode> getNodes() {
		return nodes;
	}

	public Optional<LoRaNode> getNode(String id) {
		return nodes.stream()
				.filter(n -> n.getId().equals(id))
				.findFirst();
	}

	public void detect() {
		List<SerialPort> ports = Arrays.stream(SerialPort.getCommPorts())
				.filter(port -> port.getVendorID() == NODE_VENDOR_ID)
				.filter(port -> port.getProductID() == NODE_PRODUCT_ID)
				.toList();

		for (SerialPort port : ports) {
			LoRaNodePort nodePort = LoRaNodePort.openNode(port);
			if (nodePort != null) {
				LoRaNode node = new LoRaNode(port.getSystemPortName(), nodePort);
				port.addDataListener(new LoRaNodeProviderListener(this));

				nodes.add(node);
			}
		}

		nodes.removeIf(n -> !n.getPort().getPort().isOpen());
	}

	public class LoRaNodeProviderListener implements SerialPortDataListener {
		private final LoRaNodeProvider provider;

		public LoRaNodeProviderListener(LoRaNodeProvider provider) {
			this.provider = provider;
		}

		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
				event.getSerialPort().closePort();
				provider.detect();
			}
		}
	}
}
