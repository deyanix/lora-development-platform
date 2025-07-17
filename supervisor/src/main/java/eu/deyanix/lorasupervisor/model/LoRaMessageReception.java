package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.event.rx.LoRaRxDoneEvent;
import eu.deyanix.lorasupervisor.protocol.event.rx.LoRaRxFinishEvent;

import java.time.LocalDateTime;
import java.util.Optional;

public class LoRaMessageReception {
	private final LoRaNode receiver;
	private final LocalDateTime date;
	private final int rssi;
	private final int snr;
	private final boolean successful;

	public LoRaMessageReception(LoRaRxFinishEvent event) {
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

	public int getRssi() {
		return rssi;
	}

	public int getSnr() {
		return snr;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
