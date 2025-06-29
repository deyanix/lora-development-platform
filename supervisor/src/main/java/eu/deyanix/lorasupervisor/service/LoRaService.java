package eu.deyanix.lorasupervisor.service;

import com.fazecast.jSerialComm.SerialPort;
import eu.deyanix.lorasupervisor.model.LoRaPortEvent;
import eu.deyanix.lorasupervisor.model.LoRaSerialPort;
import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaCommander;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPortListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LoRaService {
	private static final int NODE_VENDOR_ID = 0x10C4;
	private static final int NODE_PRODUCT_ID = 0xEA60;

	private final ArrayList<LoRaNode> nodes = new ArrayList<>();
	private final WebSocketService webSocketService;
	private final LoRaStatisticService loraStatisticService;

	public LoRaService(WebSocketService webSocketService, LoRaStatisticService loraStatisticService) {
		this.webSocketService = webSocketService;
		this.loraStatisticService = loraStatisticService;
	}

	public List<SerialPort> getSerialPorts() {
		return Arrays.stream(SerialPort.getCommPorts())
				.filter(port -> port.getVendorID() == NODE_VENDOR_ID)
				.filter(port -> port.getProductID() == NODE_PRODUCT_ID)
				.toList();
	}

	public List<LoRaSerialPort> getPorts() {
		return getSerialPorts().stream()
				.map(p -> new LoRaSerialPort()
						.setPortName(p.getSystemPortName())
						.setNodeId(getNodeByPort(p.getSystemPortName())
								.map(LoRaNode::getId)
								.orElse(null)))
				.toList();
	}

	public LoRaSerialPort connect(String portName) {
		SerialPort serialPort = SerialPort.getCommPort(portName);
		LoRaPort port = new LoRaPort(serialPort);
		port.addListener(new LoRaListener());
		port.addListener(loraStatisticService.getPortListener());

		if (port.open()) {
			try {
				LoRaCommander commander = port.createCommander();
				String nodeId = commander.getId();

				LoRaNode node = getNodeById(nodeId).orElse(null);
				if (node != null) {
					node.setCommander(commander);
					node.synchronizeUp();
				} else {
					node = new LoRaNode(nodeId, commander);
					node.synchronizeDown();
					nodes.add(node);
				}
				port.setNode(node);

				return new LoRaSerialPort()
						.setPortName(serialPort.getSystemPortName())
						.setNodeId(nodeId);
			} catch (Exception ex) {
				ex.printStackTrace();
				serialPort.closePort();
				disconnect(serialPort.getSystemPortName());
			}
		} else {
			serialPort.closePort();
		}
		return null;
	}

	public void disconnect(String port) {
		getNodeByPort(port)
				.flatMap(LoRaNode::getPort)
				.ifPresent(LoRaPort::close);
	}

	public List<LoRaNode> getNodes() {
		return nodes;
	}

	public Optional<LoRaNode> getNodeById(String id) {
		return nodes.stream()
				.filter(n -> n.getId().equals(id))
				.findFirst();
	}

	public Optional<LoRaNode> getNodeByPort(String port) {
		return nodes.stream()
				.filter(n -> n.getPort()
						.map(p -> p.getSerialPort().getSystemPortName().equals(port))
						.orElse(false))
				.findFirst();
	}

	protected class LoRaListener implements LoRaPortListener {
		@Override
		public void onEvent(LoRaEvent event) {
			webSocketService.sendMessage("/topic/event", event);
		}
	}
}
