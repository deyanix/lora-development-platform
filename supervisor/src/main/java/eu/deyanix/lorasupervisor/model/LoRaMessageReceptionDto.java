package eu.deyanix.lorasupervisor.model;

import java.time.LocalDateTime;

public class LoRaMessageReceptionDto {
	private long eventId;
	private String receiverId;
	private LocalDateTime date;
	private int rssi;
	private int snr;

	public long getEventId() {
		return eventId;
	}

	public LoRaMessageReceptionDto setEventId(long eventId) {
		this.eventId = eventId;
		return this;
	}

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

	public int getRssi() {
		return rssi;
	}

	public LoRaMessageReceptionDto setRssi(int rssi) {
		this.rssi = rssi;
		return this;
	}

	public int getSnr() {
		return snr;
	}

	public LoRaMessageReceptionDto setSnr(int snr) {
		this.snr = snr;
		return this;
	}
}
