package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.event.rx.LoRaRxDoneEvent;
import eu.deyanix.lorasupervisor.protocol.event.rx.LoRaRxFinishEvent;

import java.time.LocalDateTime;
import java.util.Optional;

public class LoRaMessageReception implements Comparable<LoRaMessageReception> {
	private final long eventId;
	private final LoRaNode receiver;
	private final LocalDateTime date;
	private final int rssi;
	private final int snr;
	private final boolean successful;

	public LoRaMessageReception(LoRaRxFinishEvent event) {
		this.eventId = event.getId();
		this.receiver = event.getNode();
		this.date = event.getDate();
		this.successful = event.isSuccessful();

		if (event instanceof LoRaRxDoneEvent doneEvent) {
			this.rssi = doneEvent.getRssi();
			this.snr = doneEvent.getSnr();
		} else {
			this.rssi = 0;
			this.snr = 0;
		}
	}

	public long getEventId() {
		return eventId;
	}

	public Optional<LoRaNode> getReceiver() {
		return Optional.ofNullable(receiver);
	}

	public LocalDateTime getDate() {
		return date;
	}

	public LoRaMessageReceptionDto toDto() {
		return new LoRaMessageReceptionDto()
				.setEventId(eventId)
				.setReceiverId(getReceiver()
						.map(LoRaNode::getId)
						.orElse(null))
				.setDate(date)
				.setRssi(rssi)
				.setSnr(snr);
	}

	public int getRssi() {
		return rssi;
	}

	public int getSnr() {
		return snr;
	}

	public boolean isSuccessful() {
		return successful;
	}

	@Override
	public int compareTo(LoRaMessageReception o) {
		int comparison = o.date.compareTo(this.date);
		if (comparison != 0) {
			return comparison;
		}
		return Long.compare(eventId, o.eventId);
	}
}
