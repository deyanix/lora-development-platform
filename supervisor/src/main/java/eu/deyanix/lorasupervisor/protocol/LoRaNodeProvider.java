package eu.deyanix.lorasupervisor.protocol;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import eu.deyanix.lorasupervisor.model.LoRaPortMessage;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPortListener;
import eu.deyanix.lorasupervisor.service.WebSocketService;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class LoRaNodeProvider {
	private static final int NODE_VENDOR_ID = 0x10C4;
	private static final int NODE_PRODUCT_ID = 0xEA60;
	private final ArrayList<LoRaNode> nodes = new ArrayList<>();
	private final WebSocketService webSocketService;

	public LoRaNodeProvider(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}


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
			LoRaPort nodePort = LoRaPort.openNode(port);
			if (nodePort != null) {
				nodePort.addListener(new LoRaPortListener() {
					@Override
					public void onSend(LoRaPort port, String data) {
						webSocketService.sendMessage("/topic/receivedData", new LoRaPortMessage(port.getSerialPort().getSystemPortName(), data));
					}

					@Override
					public void onReceive(LoRaPort port, String data) {
						webSocketService.sendMessage("/topic/sentData", new LoRaPortMessage(port.getSerialPort().getSystemPortName(), data));
					}
				});

				try {
					String nodeId = nodePort.createCommander().getId();

					LoRaNode node = new LoRaNode(nodeId, nodePort);
					port.addDataListener(new LoRaPortDisconnectListener());
					nodes.add(node);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	public void removeClosed() {
		nodes.removeIf(n -> !n.getPort().getSerialPort().isOpen());
	}

	@PostConstruct
	protected void init() {
		detect();
	}

	protected static class LoRaPortDisconnectListener implements SerialPortDataListener {
		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			SerialPort serialPort = event.getSerialPort();
			if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
				serialPort.closePort();
				System.out.println("DISCONNECTED PORT " + serialPort.getSystemPortName());
			}
		}
	}
}
