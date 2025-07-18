package eu.deyanix.lorasupervisor.service;

import eu.deyanix.lorasupervisor.model.LoRaMessage;
import eu.deyanix.lorasupervisor.model.LoRaMessageDto;
import eu.deyanix.lorasupervisor.model.LoRaMessageReception;
import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.event.rx.LoRaRxDoneEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxFinishEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxStartEvent;
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

	public class LoRaPortStatisticListener implements LoRaPortListener {
		@Override
		public void onEvent(LoRaEvent event) {
			if (event instanceof LoRaTxStartEvent txStartEvent) {
				messages.add(new LoRaMessage(txStartEvent));
			}

			if (event instanceof LoRaTxFinishEvent txFinishEvent) {
				messages.stream()
					.filter(msg -> msg.getEndDate() == null)
					.filter(msg -> msg.getSender()
							.filter(node -> node == txFinishEvent.getNode())
							.isPresent())
					.findFirst()
					.ifPresent(msg -> msg.finish(txFinishEvent));
			}

			if (event instanceof LoRaRxDoneEvent rxDoneEvent) {
				messages.stream()
					.filter(msg -> msg.getData().equals(rxDoneEvent.getData()))
					.findFirst()
					.ifPresent(msg -> msg.getReceptions()
							.add(new LoRaMessageReception(rxDoneEvent)));
			}
		}
	}
}
