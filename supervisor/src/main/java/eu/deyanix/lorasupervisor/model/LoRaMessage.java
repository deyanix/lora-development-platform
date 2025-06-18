package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LoRaMessage {
	private final LoRaNode sender;
	private final LocalDateTime startDate;
	private final String data;
	private final List<LoRaMessageReception> receptions = new ArrayList<>();
	private LocalDateTime endDate;

	public LoRaMessage(LoRaNode sender, LocalDateTime startDate, String data) {
		this.sender = sender;
		this.startDate = startDate;
		this.data = data;
	}

	public Optional<LoRaNode> getSender() {
		return Optional.ofNullable(sender);
	}

	public List<LoRaMessageReception> getReceptions() {
		return receptions;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public LoRaMessage setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getData() {
		return data;
	}

	public LoRaMessageDto toDto() {
		return new LoRaMessageDto()
				.setSenderId(getSender()
						.map(LoRaNode::getId)
						.orElse(null))
				.setStartDate(startDate)
				.setEndDate(endDate)
				.setData(data)
				.setReceptions(receptions.stream()
						.map(LoRaMessageReception::toDto)
						.toList());
	}
}
