package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.ArgumentData;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.CommandResult;
import eu.deyanix.lorasupervisor.protocol.command.ExtensibleStringArgument;
import eu.deyanix.lorasupervisor.protocol.config.LoRaBandwidth;
import eu.deyanix.lorasupervisor.protocol.config.LoRaCodingRate;
import eu.deyanix.lorasupervisor.protocol.config.LoRaConfiguration;
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

		port.send(tx, rx)
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

	public void setTxTimeout(int value) {
		sendDataSetter("TXTO",
				new ArgumentData().setInteger(value));
	}

	public LoRaMode getMode() {
		return sendDataGetter("MODE")
				.getString()
				.map(LoRaMode::valueOf)
				.orElseThrow();
	}

	public void setMode(LoRaMode value) {
		sendDataSetter("MODE",
				new ArgumentData().setString(value.name()));
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
				new ArgumentData().setString(value.name()));
	}


	public LoRaConfiguration getConfiguration() {
		List<ArgumentData> rtoArgs = sendDataGetter("RTO", 2);
		Integer minDelta = rtoArgs.get(0).getInteger().orElseThrow();
		Integer maxDelta = rtoArgs.get(1).getInteger().orElseThrow();

		return new LoRaConfiguration()
				.setMode(getMode())
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
				.setRxSymbolTimeout(getRxSymbolTimeout())
				.setMinDelta(minDelta)
				.setMaxDelta(maxDelta)
				.setAckRequired(getAckRequired())
				.setAckLifetime(getAckLifetime())
				.setInterval(getInterval())
				.setAuto(getAuto());
	}

	public void setConfiguration(LoRaConfiguration configuration) {
		setMode(configuration.getMode());
		setFrequency(configuration.getFrequency());
		setBandwidth(configuration.getBandwidth());
		setPower(configuration.getPower());
		setSpreadingFactor(configuration.getSpreadingFactor());
		setCodingRate(configuration.getCodingRate());
		setEnabledCrc(configuration.isEnableCrc());
		setIqInverted(configuration.isIqInverted());
		setPreambleLength(configuration.getPreambleLength());
		setPayloadLength(configuration.getPayloadLength());
		setTxTimeout(configuration.getTxTimeout());
		setRxSymbolTimeout(configuration.getRxSymbolTimeout());
		setAckRequired(configuration.getAckRequired());
		setAckLifetime(configuration.getAckLifetime());
		setInterval(configuration.getInterval());
		setAuto(configuration.getAuto());
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
				new ExtensibleStringArgument(data));

		Command rx = CommandFactory.createSetter("TX",
				new ArgumentData().setString("OK"),
				new ArgumentData().setInteger(data.length()));

		port.send(tx, rx);
	}

	public LoRaPort getPort() {
		return port;
	}
}
