package eu.deyanix.lorasupervisor.service;

import com.fazecast.jSerialComm.SerialPort;
import eu.deyanix.lorasupervisor.model.LoRaPortMessage;
import eu.deyanix.lorasupervisor.model.LoRaSerialPort;
import eu.deyanix.lorasupervisor.protocol.LoRaNode;
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

	public LoRaService(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
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
						.setPort(p.getSystemPortName())
						.setNodeId(getNodeByPort(p.getSystemPortName())
								.map(LoRaNode::getId)
								.orElse(null)))
				.toList();
	}

	public LoRaSerialPort connect(String portName) {
		SerialPort serialPort = SerialPort.getCommPort(portName);
		LoRaPort port = LoRaPort.openNode(serialPort);
		if (port != null) {
			try {
				port.addListener(new LoRaListener());

				LoRaCommander commander = port.createCommander();
				String nodeId = commander.getId();

				LoRaNode node = getNodeById(nodeId).orElse(null);
				if (node != null) {
					node.setCommander(commander);
					commander.setMode(node.getMode());
					commander.setRadioConfiguration(node.getRadioConfiguration());
					commander.setConfiguration(node.getAutoConfiguration());
					commander.setLed(node.isFlashing());
				} else {
					node = new LoRaNode(nodeId, port);
					nodes.add(node);
				}
				node.setMode(commander.getMode());
				node.setRadioConfiguration(commander.getRadioConfiguration());
				node.setAutoConfiguration(commander.getConfiguration());
				node.setFlashing(commander.isLed());

				return new LoRaSerialPort()
						.setPort(serialPort.getSystemPortName())
						.setNodeId(nodeId);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			serialPort.closePort();
		}
		return null;
	}

	public void disconnect(String port) {
		LoRaNode node = getNodeByPort(port).orElse(null);
		if (node != null) {
			node.getPort().ifPresent(p -> p.getSerialPort().closePort());
			node.setCommander(null);
		}
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
		public void onDisconnect(LoRaPort port) {
			disconnect(port.getSerialPort().getSystemPortName());
		}

		@Override
		public void onSend(LoRaPort port, String data) {
			webSocketService.sendMessage("/topic/serial/tx",
					new LoRaPortMessage(port.getSerialPort().getSystemPortName(), data));
		}

		@Override
		public void onReceive(LoRaPort port, String data) {
			webSocketService.sendMessage("/topic/serial/rx",
					new LoRaPortMessage(port.getSerialPort().getSystemPortName(), data));
		}

		@Override
		public void onRxDone(LoRaPort port, String data) {
			webSocketService.sendMessage("/topic/lora/rx/done",
					new LoRaPortMessage(port.getSerialPort().getSystemPortName(), data));
		}

		@Override
		public void onTxDone(LoRaPort port, String data) {
			webSocketService.sendMessage("/topic/lora/tx/done",
					new LoRaPortMessage(port.getSerialPort().getSystemPortName(), data));
		}
	}
}
