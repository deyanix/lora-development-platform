package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.ArgumentData;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.CommandResult;
import eu.deyanix.lorasupervisor.protocol.command.ExtensibleArgument;
import eu.deyanix.lorasupervisor.protocol.config.LoRaBandwidth;
import eu.deyanix.lorasupervisor.protocol.config.LoRaCodingRate;
import eu.deyanix.lorasupervisor.protocol.config.LoRaConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaRadioConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaMode;
import eu.deyanix.lorasupervisor.protocol.config.LoRaAuto;

import java.util.List;
import java.util.stream.IntStream;

public class LoRaCommander {
	private final LoRaPort port;

	public LoRaCommander(LoRaPort port) {
		this.port = port;
	}

	public List<ArgumentData> sendDataGetter(String name, int args) {
		Command tx = CommandFactory.createGetter(name);
		Command rx = CommandFactory.createSetterArgs(name, IntStream.range(0, args)
				.mapToObj(i -> new Argument())
				.toArray(Argument[]::new));

		return port.send(tx, rx, 3)
				.map(CommandResult::getArguments)
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

	public int getDelta() {
		return sendDataGetter("RTO")
				.getInteger()
				.orElseThrow();
	}

	public void setDelta(int value) {
		sendDataSetter("RTO",
				new ArgumentData().setInteger(value));
	}

	public boolean isBackOffIncrease() {
		return sendDataGetter("BIN")
				.getBoolean()
				.orElseThrow();
	}

	public void setBackOffIncrease(boolean value) {
		sendDataSetter("BIN",
				new ArgumentData().setBoolean(value));
	}

	public LoRaRadioConfiguration getRadioConfiguration() {
		return new LoRaRadioConfiguration()
				.setFrequency(getFrequency())
				.setBandwidth(getBandwidth())
				.setPower(getPower())
				.setSpreadingFactor(getSpreadingFactor())
				.setCodingRate(getCodingRate())
				.setEnableCrc(isEnabledCrc())
				.setIqInverted(isIqInverted())
				.setPreambleLength(getPreambleLength())
				.setPayloadLength(getPayloadLength())
				.setTxTimeout(getTxTimeout())
				.setRxSymbolTimeout(getRxSymbolTimeout());
	}

	public void setRadioConfiguration(LoRaRadioConfiguration configuration) {
		configuration.getFrequency().ifPresent(this::setFrequency);
		configuration.getBandwidth().ifPresent(this::setBandwidth);
		configuration.getPower().ifPresent(this::setPower);
		configuration.getSpreadingFactor().ifPresent(this::setSpreadingFactor);
		configuration.getCodingRate().ifPresent(this::setCodingRate);
		configuration.isEnableCrc().ifPresent(this::setEnabledCrc);
		configuration.isIqInverted().ifPresent(this::setIqInverted);
		configuration.getPreambleLength().ifPresent(this::setPreambleLength);
		configuration.getPayloadLength().ifPresent(this::setPayloadLength);
		configuration.getTxTimeout().ifPresent(this::setTxTimeout);
		configuration.getRxSymbolTimeout().ifPresent(this::setRxSymbolTimeout);
		push();
	}

	public LoRaConfiguration getConfiguration() {
		return new LoRaConfiguration()
				.setMode(getMode())
				.setDelta(getDelta())
				.setAckRequired(getAckRequired())
				.setAckLifetime(getAckLifetime())
				.setInterval(getInterval())
				.setAuto(getAuto())
				.setBackOffIncrease(isBackOffIncrease());
	}

	public void setConfiguration(LoRaConfiguration configuration) {
		configuration.getMode().ifPresent(this::setMode);
		configuration.getDelta().ifPresent(this::setDelta);
		configuration.getAuto().ifPresent(this::setAuto);
		configuration.isAckRequired().ifPresent(this::setAckRequired);
		configuration.getAckLifetime().ifPresent(this::setAckLifetime);
		configuration.getInterval().ifPresent(this::setInterval);
		configuration.isBackOffIncrease().ifPresent(this::setBackOffIncrease);
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
}
