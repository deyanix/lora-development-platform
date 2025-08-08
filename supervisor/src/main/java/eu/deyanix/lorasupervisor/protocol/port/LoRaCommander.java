package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.ArgumentData;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.ExtensibleArgument;
import eu.deyanix.lorasupervisor.protocol.config.*;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LoRaCommander {
	public static final List<LoRaCommanderPropertyFactor<LoRaConfiguration, ?>> CONFIGURATION_PROPERTY_FACTORS = List.of(
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::getMode, LoRaConfiguration::setMode, LoRaCommander::getMode, LoRaCommander::setMode),
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::getInitialBackoffMax, LoRaConfiguration::setInitialBackoffMax, LoRaCommander::getInitialBackoffMax, LoRaCommander::setInitialBackoffMax),
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::getAuto, LoRaConfiguration::setAuto, LoRaCommander::getAuto, LoRaCommander::setAuto),
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::isAckRequired, LoRaConfiguration::setAckRequired, LoRaCommander::getAckRequired, LoRaCommander::setAckRequired),
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::getAckLifetime, LoRaConfiguration::setAckLifetime, LoRaCommander::getAckLifetime, LoRaCommander::setAckLifetime),
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::getInterval, LoRaConfiguration::setInterval, LoRaCommander::getInterval, LoRaCommander::setInterval),
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::isBackoffIncrease, LoRaConfiguration::setBackoffIncrease, LoRaCommander::isBackoffIncrease, LoRaCommander::setBackoffIncrease),
			LoRaCommanderPropertyFactor.of(LoRaConfiguration::getRandomDistribution, LoRaConfiguration::setRandomDistribution, LoRaCommander::getRandomDistribution, LoRaCommander::setRandomDistribution)
	);

	public static final List<LoRaCommanderPropertyFactor<LoRaRadioConfiguration, ?>> RADIO_CONFIGURATION_PROPERTY_FACTORS = List.of(
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getFrequency, LoRaRadioConfiguration::setFrequency, LoRaCommander::getFrequency, LoRaCommander::setFrequency),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getBandwidth, LoRaRadioConfiguration::setBandwidth, LoRaCommander::getBandwidth, LoRaCommander::setBandwidth),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getPower, LoRaRadioConfiguration::setPower, LoRaCommander::getPower, LoRaCommander::setPower),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getSpreadingFactor, LoRaRadioConfiguration::setSpreadingFactor, LoRaCommander::getSpreadingFactor, LoRaCommander::setSpreadingFactor),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getCodingRate, LoRaRadioConfiguration::setCodingRate, LoRaCommander::getCodingRate, LoRaCommander::setCodingRate),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::isEnableCrc, LoRaRadioConfiguration::setEnableCrc, LoRaCommander::isEnabledCrc, LoRaCommander::setEnabledCrc),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::isIqInverted, LoRaRadioConfiguration::setIqInverted, LoRaCommander::isIqInverted, LoRaCommander::setIqInverted),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getPreambleLength, LoRaRadioConfiguration::setPreambleLength, LoRaCommander::getPreambleLength, LoRaCommander::setPreambleLength),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getPayloadLength, LoRaRadioConfiguration::setPayloadLength, LoRaCommander::getPayloadLength, LoRaCommander::setPayloadLength),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getTxTimeout, LoRaRadioConfiguration::setTxTimeout, LoRaCommander::getTxTimeout, LoRaCommander::setTxTimeout),
			LoRaCommanderPropertyFactor.of(LoRaRadioConfiguration::getRxSymbolTimeout, LoRaRadioConfiguration::setRxSymbolTimeout, LoRaCommander::getRxSymbolTimeout, LoRaCommander::setRxSymbolTimeout)
	);

	public static void mergeRadioConfiguration(LoRaRadioConfiguration target, LoRaRadioConfiguration source) {
		RADIO_CONFIGURATION_PROPERTY_FACTORS.forEach(
				property -> property.merge(target, source));
	}

	public static void mergeConfiguration(LoRaConfiguration target, LoRaConfiguration source) {
		CONFIGURATION_PROPERTY_FACTORS.forEach(
				property -> property.merge(target, source));
	}

	private final LoRaPort port;

	public LoRaCommander(LoRaPort port) {
		this.port = port;
	}

	public ArgumentData sendDataGetter(String name, ArgumentData... args) {
		Command tx = CommandFactory.createGetter(name, args);
		Command rx = CommandFactory.createSetterArgs(name, new Argument());

		return port.send(tx, rx, 3)
				.flatMap(cmd -> cmd.getArgument(0))
				.orElseThrow(); // TODO: Internal exception
	}

	public ArgumentData sendDataGetter(String name) {
		Command tx = CommandFactory.createGetterArgs(name);
		Command rx = CommandFactory.createSetterArgs(name, new Argument());

		return port.send(tx, rx, 3)
				.flatMap(cmd -> cmd.getArgument(0))
				.orElseThrow();
	}

	public void sendDataSetter(String name, ArgumentData... args) {
		Command tx = CommandFactory.createSetter(name, args);
		Command rx = CommandFactory.createSetterArgs(name, new Argument());

		port.send(tx, rx, 3)
				.flatMap(cmd -> cmd.getArgument(0))
				.flatMap(ArgumentData::getString)
				.filter(val -> val.equals("OK"))
				.orElseThrow();
	}

	public String getId() {
		return sendDataGetter("ID")
				.getString()
				.orElseThrow();
	}

	public long getFrequency() {
		return sendDataGetter("FRQ")
				.getLong()
				.orElseThrow();
	}

	public void setFrequency(long value) {
		sendDataSetter("FRQ",
				new ArgumentData().setLong(value));
	}

	public LoRaBandwidth getBandwidth() {
		return sendDataGetter("BW")
				.getInteger()
				.map(LoRaBandwidth::valueOf)
				.orElseThrow();
	}

	public void setBandwidth(LoRaBandwidth value) {
		sendDataSetter("BW",
				new ArgumentData().setInteger(value.getValue()));
	}

	public int getSpreadingFactor() {
		return sendDataGetter("SF")
				.getInteger()
				.orElseThrow();
	}

	public void setSpreadingFactor(int value) {
		sendDataSetter("SF",
				new ArgumentData().setInteger(value));
	}

	public int getPower() {
		return sendDataGetter("PWR")
				.getInteger()
				.orElseThrow();
	}

	public void setPower(int value) {
		sendDataSetter("PWR",
				new ArgumentData().setInteger(value));
	}

	public LoRaCodingRate getCodingRate() {
		return sendDataGetter("CRT")
				.getInteger()
				.map(LoRaCodingRate::valueOf)
				.orElseThrow();
	}

	public void setCodingRate(LoRaCodingRate value) {
		sendDataSetter("CRT",
				new ArgumentData().setInteger(value.getValue()));
	}

	public Integer getPreambleLength() {
		return sendDataGetter("PRLEN")
				.getInteger()
				.orElseThrow();
	}

	public void setPreambleLength(int value) {
		sendDataSetter("PRLEN",
				new ArgumentData().setInteger(value));
	}

	public Integer getPayloadLength() {
		return sendDataGetter("PYLEN")
				.getInteger()
				.orElseThrow();
	}

	public void setPayloadLength(int value) {
		sendDataSetter("PYLEN",
				new ArgumentData().setInteger(value));
	}

	public boolean isEnabledCrc() {
		return sendDataGetter("CRC")
				.getBoolean()
				.orElseThrow();
	}

	public void setEnabledCrc(boolean value) {
		sendDataSetter("CRC",
				new ArgumentData().setBoolean(value));
	}

	public boolean isIqInverted() {
		return sendDataGetter("IIQ")
				.getBoolean()
				.orElseThrow();
	}

	public void setIqInverted(boolean value) {
		sendDataSetter("IIQ",
				new ArgumentData().setBoolean(value));
	}

	public int getRxSymbolTimeout() {
		return sendDataGetter("STO")
				.getInteger()
				.orElseThrow();
	}

	public void setRxSymbolTimeout(int value) {
		sendDataSetter("STO",
				new ArgumentData().setInteger(value));
	}

	public int getTxTimeout() {
		return sendDataGetter("TXTO")
				.getInteger()
				.orElseThrow();
	}

	public void push() {
		sendDataSetter("PUSH");
	}

	public void setTxTimeout(int value) {
		sendDataSetter("TXTO",
				new ArgumentData().setInteger(value));
	}

	public LoRaMode getMode() {
		return sendDataGetter("MODE")
				.getString()
				.map(LoRaMode::getEnum)
				.orElseThrow();
	}

	public void setMode(LoRaMode value) {
		sendDataSetter("MODE",
				new ArgumentData().setString(value.getValue()));
	}

	public boolean getAckRequired() {
		return sendDataGetter("ACKRQ")
				.getBoolean()
				.orElseThrow();
	}

	public void setAckRequired(boolean value) {
		sendDataSetter("ACKRQ",
				new ArgumentData().setBoolean(value));
	}

	public int getAckLifetime() {
		return sendDataGetter("ACKLT")
				.getInteger()
				.orElseThrow();
	}

	public void setAckLifetime(int value) {
		sendDataSetter("ACKLT",
				new ArgumentData().setInteger(value));
	}

	public long getInterval() {
		return sendDataGetter("INV")
				.getLong()
				.orElseThrow();
	}

	public void setInterval(long value) {
		sendDataSetter("INV",
				new ArgumentData().setLong(value));
	}

	public LoRaAuto getAuto() {
		return sendDataGetter("AUTO")
				.getString()
				.map(LoRaAuto::valueOf)
				.orElseThrow();
	}

	public void setAuto(LoRaAuto value) {
		sendDataSetter("AUTO",
				new ArgumentData(value.name()));
	}

	public void resetAuto() {
		sendDataSetter("AUTO",
				new ArgumentData("RST"));
	}

	public int getInitialBackoffMax() {
		return sendDataGetter("IBM")
				.getInteger()
				.orElseThrow();
	}

	public void setInitialBackoffMax(int value) {
		sendDataSetter("IBM",
				new ArgumentData().setInteger(value));
	}

	public boolean isBackoffIncrease() {
		return sendDataGetter("BIN")
				.getBoolean()
				.orElseThrow();
	}

	public void setBackoffIncrease(boolean value) {
		sendDataSetter("BIN",
				new ArgumentData().setBoolean(value));
	}

	public LoRaRadioConfiguration getRadioConfiguration() {
		LoRaRadioConfiguration configuration = new LoRaRadioConfiguration();
		RADIO_CONFIGURATION_PROPERTY_FACTORS.forEach(
				property -> property.syncDown(this, configuration));

		return configuration;
	}

	public void setRadioConfiguration(LoRaRadioConfiguration configuration, LoRaRadioConfiguration previousConfiguration) {
		RADIO_CONFIGURATION_PROPERTY_FACTORS.forEach(
				property -> property.syncUp(this, configuration, previousConfiguration));

		push();
	}

	public void setRadioConfiguration(LoRaRadioConfiguration configuration) {
		setRadioConfiguration(configuration, null);
	}

	public LoRaConfiguration getConfiguration() {
		LoRaConfiguration configuration = new LoRaConfiguration();
		CONFIGURATION_PROPERTY_FACTORS.forEach(
				property -> property.syncDown(this, configuration));

		return configuration;
	}

	public void setConfiguration(LoRaConfiguration configuration, LoRaConfiguration previousConfiguration) {
		CONFIGURATION_PROPERTY_FACTORS.forEach(
				property -> property.syncUp(this, configuration, previousConfiguration));
	}

	public void setConfiguration(LoRaConfiguration configuration) {
		setConfiguration(configuration, null);
	}

	public void setLed(boolean value) {
		sendDataSetter("LED",
				new ArgumentData().setBoolean(value));
	}

	public boolean isLed() {
		return sendDataGetter("LED")
				.getBoolean()
				.orElseThrow();
	}

	public long getTimeOnAir(int length) {
		return sendDataGetter("TOA", new ArgumentData().setInteger(length))
				.getLong()
				.orElseThrow();
	}

	public LoRaRandomDistribution getRandomDistribution() {
		return sendDataGetter("RNDST")
				.getString()
				.map(LoRaRandomDistribution::getEnum)
				.orElseThrow();
	}

	public void setRandomDistribution(LoRaRandomDistribution value) {
		sendDataSetter("RNDST",
				new ArgumentData().setString(value.getValue()));
	}

	public void transmit(String data) {
		Command tx = CommandFactory.createSetterArgs("TX",
				new ExtensibleArgument(data));

		Command rx = CommandFactory.createSetter("TX",
				new ArgumentData().setString("OK"),
				new ArgumentData().setInteger(data.length()));

		port.send(tx, rx);
	}

	public LoRaPort getPort() {
		return port;
	}

	public static class LoRaCommanderPropertyFactor<C, T> {
		public static <C, T> LoRaCommanderPropertyFactor<C, T> of(Function<C, Optional<T>> getter, BiConsumer<C, T> setter, Function<LoRaCommander, T> commanderGetter, BiConsumer<LoRaCommander, T> commanderSetter) {
			return new LoRaCommanderPropertyFactor<>(getter, setter, commanderGetter, commanderSetter);
		}

		private final Function<C, Optional<T>> configGetter;
		private final BiConsumer<C, T> configSetter;
		private final Function<LoRaCommander, T> commanderGetter;
		private final BiConsumer<LoRaCommander, T> commanderSetter;

		public LoRaCommanderPropertyFactor(Function<C, Optional<T>> configGetter, BiConsumer<C, T> configSetter, Function<LoRaCommander, T> commanderGetter, BiConsumer<LoRaCommander, T> commanderSetter) {
			this.configGetter = configGetter;
			this.configSetter = configSetter;
			this.commanderGetter = commanderGetter;
			this.commanderSetter = commanderSetter;
		}

		public void syncUp(LoRaCommander commander, C config, C previousConfig) {
			Optional.ofNullable(config)
					.flatMap(configGetter)
					.ifPresent(newValue -> {
						boolean shouldSet = Optional.ofNullable(previousConfig)
								.flatMap(configGetter)
								.map(oldValue -> !newValue.equals(oldValue))
								.orElse(true);

						if (shouldSet) {
							commanderSetter.accept(commander, newValue);
						}
					});
		}

		public void syncUp(LoRaCommander commander, C config) {
			syncUp(commander, config, null);
		}

		public void syncDown(LoRaCommander commander, C config) {
			T nodeValue = commanderGetter.apply(commander);
			configSetter.accept(config, nodeValue);
		}

		public void merge(C targetConfig, C sourceConfig) {
			Optional.ofNullable(sourceConfig)
					.flatMap(configGetter)
					.ifPresent(value -> configSetter.accept(targetConfig, value));
		}
	}
}
