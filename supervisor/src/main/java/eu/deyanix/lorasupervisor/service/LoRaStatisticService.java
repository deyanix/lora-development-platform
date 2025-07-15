package eu.deyanix.lorasupervisor.service;

import eu.deyanix.lorasupervisor.model.LoRaMessage;
import eu.deyanix.lorasupervisor.model.LoRaMessageDto;
import eu.deyanix.lorasupervisor.model.LoRaMessageReception;
import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPortListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoRaStatisticService {
	private final LoRaPortStatisticListener listener = new LoRaPortStatisticListener();
	private final List<LoRaMessage> messages = new ArrayList<>();

	public List<LoRaMessageDto> getMessagesBySenderId(String id) {
		return messages.stream()
				.filter(msg -> msg.getSender()
						.filter(sender -> sender.getId().equals(id))
						.isPresent())
				.map(LoRaMessage::toDto)
				.toList();
	}

	public void reset() {
		messages.clear();
	}

	public LoRaPortListener getPortListener() {
		return listener;
	}

	public static class LoRaPortStatisticListener implements LoRaPortListener {
//		@Override
//		public void onTxStart(LoRaPort port, String data) {
//			messages.add(new LoRaMessage(port.getNode(), LocalDateTime.now(), data));
//		}
//
//		@Override
//		public void onTxDone(LoRaPort port) {
//			messages.stream()
//					.filter(msg -> msg.getEndDate() == null)
//					.filter(msg -> msg.getSender()
//							.filter(node -> node == port.getNode())
//							.isPresent())
//					.findFirst()
//					.ifPresent(msg -> msg.setEndDate(LocalDateTime.now()));
//		}
//
//		@Override
//		public void onRxDone(LoRaPort port, int rssi, int snr, String data) {
//			messages.stream()
//					.filter(msg -> msg.getData().equals(data))
//					.findFirst()
//					.ifPresent(msg -> msg.getReceptions()
//							.add(new LoRaMessageReception(port.getNode(), LocalDateTime.now())));
//		}

		@Override
		public void onEvent(LoRaEvent event) {

		}
	}
}
