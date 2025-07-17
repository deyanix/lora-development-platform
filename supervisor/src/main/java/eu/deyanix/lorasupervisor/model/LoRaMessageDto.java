package eu.deyanix.lorasupervisor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoRaMessageDto {
	private String senderId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String data;
	private boolean successful;
	private long duration;
	private List<LoRaMessageReceptionDto> receptions = new ArrayList<>();

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
