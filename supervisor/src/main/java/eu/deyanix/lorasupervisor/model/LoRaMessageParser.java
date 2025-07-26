package eu.deyanix.lorasupervisor.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoRaMessageParser {
	public static final Pattern MESSAGE_PATTERN =
			Pattern.compile("^(?<ackres>ACK-)?(?<id>[0-9A-Fa-f]+)-(?<seq>\\d+)(?<ackreq>-\\?)?$");

	private final String data;
	private final Matcher matcher;

	public LoRaMessageParser(String data) {
		this.data = data;
		this.matcher = MESSAGE_PATTERN.matcher(data);
	}

	public boolean isValid() {
		return matcher.matches();
	}

	public boolean isAckResponse() {
		return isValid() && matcher.group("ackres") != null;
	}

	public boolean isAckRequest() {
		return isValid() && matcher.group("ackreq") != null;
	}

	public String getId() {
		if (isValid()) {
			return matcher.group("id");
		}
		return null;
	}

	public long getSequence() {
		if (isValid()) {
			try {
				return Long.parseLong(matcher.group("seq"));
			} catch (NumberFormatException ignored) {
			}
		}
		return -1;
	}
}
