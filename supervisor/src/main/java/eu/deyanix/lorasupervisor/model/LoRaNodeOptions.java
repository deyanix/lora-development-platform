package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.config.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class LoRaNodeOptions {
	public LoRaNodeOptionRange<Long> frequency =
			new LoRaNodeOptionRange<>(LoRaRadioConfiguration.FREQUENCY_MIN, LoRaRadioConfiguration.FREQUENCY_MAX);
	public LoRaNodeOptionRange<Integer> power =
			new LoRaNodeOptionRange<>(LoRaRadioConfiguration.POWER_MIN, LoRaRadioConfiguration.POWER_MAX);
	public LoRaNodeOptionRange<Integer> preambleLength =
			new LoRaNodeOptionRange<>(LoRaRadioConfiguration.PREAMBLE_LENGTH_MIN, LoRaRadioConfiguration.PREAMBLE_LENGTH_MAX);
	public LoRaNodeOptionRange<Integer> payloadLength =
			new LoRaNodeOptionRange<>(LoRaRadioConfiguration.PAYLOAD_LENGTH_MIN, LoRaRadioConfiguration.PAYLOAD_LENGTH_MAX);
	public LoRaNodeOptionRange<Integer> txTimeout =
			new LoRaNodeOptionRange<>(LoRaRadioConfiguration.TX_TIMEOUT_MIN, LoRaRadioConfiguration.TX_TIMEOUT_MAX);
	public LoRaNodeOptionRange<Integer> rxSymbolTimeout =
			new LoRaNodeOptionRange<>(LoRaRadioConfiguration.RX_TIMEOUT_MIN, LoRaRadioConfiguration.RX_TIMEOUT_MAX);

	public List<LoRaNodeOption<Integer>> spreadingFactor = IntStream
			.rangeClosed(LoRaRadioConfiguration.SPREADING_FACTOR_MIN, LoRaRadioConfiguration.SPREADING_FACTOR_MAX)
			.mapToObj(val -> new LoRaNodeOption<>(val, "SF"+val))
			.toList();
	public List<LoRaNodeOption<LoRaBandwidth>> bandwidth = Arrays.stream(LoRaBandwidth.values())
			.map(val -> new LoRaNodeOption<>(val, val.toString()))
			.toList();
	public List<LoRaNodeOption<LoRaCodingRate>> codingRate = Arrays.stream(LoRaCodingRate.values())
			.map(val -> new LoRaNodeOption<>(val, val.toString()))
			.toList();

	public List<String> mode = Arrays.stream(LoRaMode.values())
			.map(Enum::name)
			.toList();
	public List<String> auto = Arrays.stream(LoRaAuto.values())
			.map(Enum::name)
			.toList();
	public List<String> randomDistribution = Arrays.stream(LoRaRandomDistribution.values())
			.map(Enum::name)
			.toList();



	public static class LoRaNodeOptionRange<T> {
		private final T min;
		private final T max;

		public LoRaNodeOptionRange(T min, T max) {
			this.min = min;
			this.max = max;
		}

		public T getMin() {
			return min;
		}

		public T getMax() {
			return max;
		}
	}

	public static class LoRaNodeOption<T> {
		private final T value;
		private final String name;

		public LoRaNodeOption(T value, String name) {
			this.value = value;
			this.name = name;
		}

		public T getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
	}
}
