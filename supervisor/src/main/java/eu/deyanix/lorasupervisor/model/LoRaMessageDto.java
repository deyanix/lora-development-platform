package eu.deyanix.lorasupervisor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoRaMessageDto {
	public long eventId;
	private String senderId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String data;
	private boolean autoMessage;
	private String destinationId;
	private boolean ackRequest;
	private boolean ackResponse;
	private boolean successful;
	private long duration;
	private List<LoRaMessageReceptionDto> receptions = new ArrayList<>();

	public long getEventId() {
		return eventId;
	}

	public LoRaMessageDto setEventId(long eventId) {
		this.eventId = eventId;
		return this;
	}

	public String getSenderId() {
		return senderId;
	}

	public LoRaMessageDto setSenderId(String senderId) {
		this.senderId = senderId;
		return this;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LoRaMessageDto setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
		return this;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public LoRaMessageDto setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean isFinished() {
		return endDate != null;
	}

	public String getData() {
		return data;
	}

	public LoRaMessageDto setData(String data) {
		this.data = data;
		return this;
	}

	public boolean isAutoMessage() {
		return autoMessage;
	}

	public LoRaMessageDto setAutoMessage(boolean autoMessage) {
		this.autoMessage = autoMessage;
		return this;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public LoRaMessageDto setDestinationId(String destinationId) {
		this.destinationId = destinationId;
		return this;
	}

	public boolean isAckRequest() {
		return ackRequest;
	}

	public LoRaMessageDto setAckRequest(boolean ackRequest) {
		this.ackRequest = ackRequest;
		return this;
	}

	public boolean isAckResponse() {
		return ackResponse;
	}

	public LoRaMessageDto setAckResponse(boolean ackResponse) {
		this.ackResponse = ackResponse;
		return this;
	}

	public List<LoRaMessageReceptionDto> getReceptions() {
		return receptions;
	}

	public LoRaMessageDto setReceptions(List<LoRaMessageReceptionDto> receptions) {
		this.receptions = receptions;
		return this;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public LoRaMessageDto setSuccessful(boolean successful) {
		this.successful = successful;
		return this;
	}

	public long getDuration() {
		return duration;
	}

	public LoRaMessageDto setDuration(long duration) {
		this.duration = duration;
		return this;
	}
}
