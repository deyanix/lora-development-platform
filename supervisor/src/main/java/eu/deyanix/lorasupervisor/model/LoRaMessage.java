package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxDoneEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxFinishEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxStartEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoRaMessage {
	private final LoRaNode sender;
	private final LocalDateTime startDate;
	private final String data;
	private final List<LoRaMessageReception> receptions = new ArrayList<>();
	private LocalDateTime endDate;
	private boolean successful = false;
	private long duration = 0;

	public LoRaMessage(LoRaTxStartEvent event) {
		this.sender = event.getNode();
		this.startDate = event.getDate();
		this.data = event.getData();
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

	public boolean isSuccessful() {
		return successful;
	}

	public long getDuration() {
		return duration;
	}

	public void finish(LoRaTxFinishEvent event) {
		this.endDate = event.getDate();
		this.successful = true;
		if (event instanceof LoRaTxDoneEvent doneEvent) {
			this.duration = doneEvent.getDuration();
		}
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
				.setSuccessful(successful)
				.setDuration(duration)
				.setData(data)
				.setReceptions(receptions.stream()
						.map(LoRaMessageReception::toDto)
						.toList());
	}
}
