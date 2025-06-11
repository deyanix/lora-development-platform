package eu.deyanix.lorasupervisor.protocol;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.DataArgument;
import eu.deyanix.lorasupervisor.protocol.connection.LoRaCommandConnection;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
				LoRaCommandConnection nodeIdConn = nodePort.sendGetter("ID");
				LoRaCommandConnection nodeFrequencyConn = nodePort.sendGetter("FRQ");

				String nodeId = nodeIdConn.get(500)
						.map(con -> con.getArgument(0))
						.flatMap(Argument::getString)
						.orElse(null);
				System.out.println("ID = " + nodeId);

				Integer nodeFrequency = nodeFrequencyConn.get(500)
						.map(con -> con.getArgument(0))
						.flatMap(Argument::getInteger)
						.orElse(null);
				System.out.println("Frequency = " + nodeFrequency);


				if (nodeId != null) {
					LoRaNode node = new LoRaNode(nodeId, nodePort);
					port.addDataListener(new SerialPortDisconnectListener());

					nodes.add(node);
				}
			}
		}

		nodes.removeIf(n -> !n.getPort().getSerialPort().isOpen());
	}

	@PostConstruct
	protected void init() {
		detect();
	}

	@Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
	protected void scheduledDetect() {
		detect();
	}

	protected static class SerialPortDisconnectListener implements SerialPortDataListener {
		@Override
		public int getListeningEvents() {
			return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
		}

		@Override
		public void serialEvent(SerialPortEvent event) {
			SerialPort serialPort = event.getSerialPort();
			if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
				serialPort.closePort();
			}
		}
	}
}
