package eu.deyanix.lorasupervisor.service;

import com.fazecast.jSerialComm.SerialPort;
import eu.deyanix.lorasupervisor.model.LoRaPortEvent;
import eu.deyanix.lorasupervisor.model.LoRaPortMessage;
import eu.deyanix.lorasupervisor.model.LoRaPortReceivedMessage;
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
						.setPortName(p.getSystemPortName())
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
					node.synchronizeUp();
				} else {
					node = new LoRaNode(nodeId, port);
					node.synchronizeDown();
					nodes.add(node);
				}

				webSocketService.sendMessage("/topic/port/connect",
						new LoRaPortEvent(serialPort.getSystemPortName()));

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
		LoRaNode node = getNodeByPort(port).orElse(null);
		if (node != null) {
			node.getPort()
					.map(LoRaPort::getSerialPort)
					.ifPresent(SerialPort::closePort);

			node.setCommander(null);

			webSocketService.sendMessage("/topic/port/disconnect",
					new LoRaPortEvent(port));
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
		public void onRxDone(LoRaPort port, int rssi, int snr, String data) {
			webSocketService.sendMessage("/topic/port/rx/done",
					new LoRaPortReceivedMessage(port.getSerialPort().getSystemPortName(), rssi, snr, data));
		}

		@Override
		public void onRxTimeout(LoRaPort port) {
			webSocketService.sendMessage("/topic/port/rx/timeout",
					new LoRaPortEvent(port.getSerialPort().getSystemPortName()));
		}

		@Override
		public void onRxError(LoRaPort port) {
			webSocketService.sendMessage("/topic/port/rx/error",
					new LoRaPortEvent(port.getSerialPort().getSystemPortName()));
		}

		@Override
		public void onTxDone(LoRaPort port) {
			webSocketService.sendMessage("/topic/port/tx/done",
					new LoRaPortEvent(port.getSerialPort().getSystemPortName()));
		}

		@Override
		public void onTxTimeout(LoRaPort port) {
			webSocketService.sendMessage("/topic/port/tx/timeout",
					new LoRaPortEvent(port.getSerialPort().getSystemPortName()));
		}

		@Override
		public void onTxStart(LoRaPort port, String data) {
			webSocketService.sendMessage("/topic/port/tx/start",
					new LoRaPortMessage(port.getSerialPort().getSystemPortName(), data));
		}

		@Override
		public void onRxStart(LoRaPort port) {
			webSocketService.sendMessage("/topic/port/rx/start",
					new LoRaPortEvent(port.getSerialPort().getSystemPortName()));
		}
	}
}
