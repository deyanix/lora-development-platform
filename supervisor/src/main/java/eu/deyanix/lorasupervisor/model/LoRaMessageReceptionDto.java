package eu.deyanix.lorasupervisor.model;

import java.time.LocalDateTime;

public class LoRaMessageReceptionDto {
	private String receiverId;
	private LocalDateTime date;

	public String getReceiverId() {
		return receiverId;
	}

	public LoRaMessageReceptionDto setReceiverId(String receiverId) {
		this.receiverId = receiverId;
		return this;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public LoRaMessageReceptionDto setDate(LocalDateTime date) {
		this.date = date;
		return this;
	}
}
