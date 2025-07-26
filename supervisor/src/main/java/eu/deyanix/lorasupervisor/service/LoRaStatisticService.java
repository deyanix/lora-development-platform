package eu.deyanix.lorasupervisor.service;

import eu.deyanix.lorasupervisor.model.LoRaMessage;
import eu.deyanix.lorasupervisor.model.LoRaMessageDto;
import eu.deyanix.lorasupervisor.model.LoRaMessageReception;
import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.event.rx.LoRaRxDoneEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxFinishEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxStartEvent;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPortListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class LoRaStatisticService {
	private final WebSocketService webSocketService;
	private final LoRaPortStatisticListener listener = new LoRaPortStatisticListener();
	private final Set<LoRaMessage> messages = new ConcurrentSkipListSet<>();

	public LoRaStatisticService(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	public List<LoRaMessageDto> getMessages() {
		return messages.stream()
				.filter(msg -> Duration.between(msg.getStartDate(), LocalDateTime.now()).toMinutes() < 2)
				.map(LoRaMessage::toDto)
				.sorted(Comparator.comparing(LoRaMessageDto::getSenderId))
				.toList();
	}

	public List<LoRaMessageDto> getMessagesBySenderId(String id) {
		return messages.stream()
				.filter(msg -> Duration.between(msg.getStartDate(), LocalDateTime.now()).toMinutes() < 2)
				.filter(msg -> msg.getSender()
						.filter(sender -> sender.getId().equals(id))
						.isPresent())
				.map(LoRaMessage::toDto)
				.sorted(Comparator.comparing(LoRaMessageDto::getSenderId))
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
			LoRaMessage message = null;

			if (event instanceof LoRaTxStartEvent txStartEvent) {
				message = new LoRaMessage(txStartEvent);
				messages.add(message);
			}

			if (event instanceof LoRaTxFinishEvent txFinishEvent) {
				message = messages.stream()
						.filter(msg -> msg.getEndDate() == null)
						.filter(msg -> msg.getSender()
								.filter(node -> node == txFinishEvent.getNode())
								.isPresent())
						.findFirst()
						.orElse(null);

				if (message != null) {
					message.finish(txFinishEvent);
				}
			}

			if (event instanceof LoRaRxDoneEvent rxDoneEvent) {
				message = messages.stream()
						.filter(msg -> msg.getData().equals(rxDoneEvent.getData()))
						.findFirst()
						.orElse(null);

				if (message != null) {
					LoRaMessageReception reception = new LoRaMessageReception(rxDoneEvent);
					message.getReceptions().add(reception);
				}
			}

			if (message != null) {
				webSocketService.sendMessage("/topic/message", message.toDto());
			}
		}
	}
}
