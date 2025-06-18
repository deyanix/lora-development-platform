package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;

import java.time.LocalDateTime;
import java.util.Optional;

public class LoRaMessageReception {
	private final LoRaNode receiver;
	private final LocalDateTime date;

	public LoRaMessageReception(LoRaNode receiver, LocalDateTime date) {
		this.receiver = receiver;
		this.date = date;
	}

	public Optional<LoRaNode> getReceiver() {
		return Optional.ofNullable(receiver);
	}

	public LocalDateTime getDate() {
		return date;
	}

	public LoRaMessageReceptionDto toDto() {
		return new LoRaMessageReceptionDto()
				.setReceiverId(getReceiver()
						.map(LoRaNode::getId)
						.orElse(null))
				.setDate(date);
	}
}
