package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.event.LoRaEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxDoneEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxFinishEvent;
import eu.deyanix.lorasupervisor.protocol.event.tx.LoRaTxStartEvent;
import org.springframework.util.comparator.Comparators;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

public class LoRaMessage implements Comparable<LoRaMessage> {
	private final long eventId;
	private final LoRaNode sender;
	private final LocalDateTime startDate;
	private final LoRaMessageParser data;
	private final Set<LoRaMessageReception> receptions = new ConcurrentSkipListSet<>();
	private LocalDateTime endDate;
	private boolean successful = false;
	private long duration = 0;

	public LoRaMessage(LoRaTxStartEvent event) {
		this.eventId = event.getId();
		this.sender = event.getNode();
		this.startDate = event.getDate();
		this.data = new LoRaMessageParser(event.getData());
	}

	public long getEventId() {
		return eventId;
	}

	public boolean isAuto() {
		return data.isValid();
	}

	public String getDestinationId() {
		return data.getId();
	}

	public boolean isAckRequest() {
		return data.isAckRequest();
	}

	public boolean isAckResponse() {
		return data.isAckResponse();
	}

	public Optional<LoRaNode> getSender() {
		return Optional.ofNullable(sender);
	}

	public Set<LoRaMessageReception> getReceptions() {
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
		this.successful = event.isSuccessful();
		if (event instanceof LoRaTxDoneEvent doneEvent) {
			this.duration = doneEvent.getDuration();
			this.endDate = startDate.plus(Duration.ofMillis(doneEvent.getDuration()));
		} else {
			this.endDate = event.getDate();
		}
	}

	public String getData() {
		return data.getData();
	}

	public LoRaMessageDto toDto() {
		return new LoRaMessageDto()
				.setEventId(eventId)
				.setSenderId(getSender()
						.map(LoRaNode::getId)
						.orElse(null))
				.setStartDate(startDate)
				.setEndDate(endDate)
				.setSuccessful(successful)
				.setDuration(duration)
				.setData(data.getData())
				.setDestinationId(getDestinationId())
				.setAutoMessage(isAuto())
				.setAckRequest(isAckRequest())
				.setAckResponse(isAckResponse())
				.setReceptions(receptions.stream()
						.map(LoRaMessageReception::toDto)
						.toList());
	}

	@Override
	public int compareTo(LoRaMessage o) {
		int comparison = o.startDate.compareTo(this.startDate);
		if (comparison != 0) {
			return comparison;
		}
		return Long.compare(eventId, o.eventId);
	}
}
