package eu.deyanix.lorasupervisor.protocol.port;

import eu.deyanix.lorasupervisor.protocol.command.Argument;
import eu.deyanix.lorasupervisor.protocol.command.Command;
import eu.deyanix.lorasupervisor.protocol.command.CommandFactory;
import eu.deyanix.lorasupervisor.protocol.command.DataArgument;
import eu.deyanix.lorasupervisor.protocol.config.LoRaBandwidth;
import eu.deyanix.lorasupervisor.protocol.config.LoRaCodingRate;
import eu.deyanix.lorasupervisor.protocol.config.LoRaConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaMode;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class LoRaCommander {
	private final LoRaPort port;

	public LoRaCommander(LoRaPort port) {
		this.port = port;
	}

	public List<Argument> sendDataGetter(String name, int args) {
		Command tx = CommandFactory.createGetter(name);
		Command rx = CommandFactory.createSetter(name, IntStream.range(0, args)
				.mapToObj(i -> new DataArgument())
				.toArray(DataArgument[]::new));

		return port.send(tx, rx, 3)
				.map(Command::getArguments)
				.orElseThrow(); // TODO: Internal exception
	}

	public Argument sendDataGetter(String name) {
		Command tx = CommandFactory.createGetter(name);
		Command rx = CommandFactory.createSetter(name, new DataArgument());

		return port.send(tx, rx, 3)
				.map(cmd -> cmd.getArgument(0))
				.orElseThrow();
	}

	public void sendDataSetter(String name, Argument... args) {
		Command tx = CommandFactory.createSetter(name, args);
		Command rx = CommandFactory.createSetter(name, new DataArgument());

		port.send(tx, rx)
				.map(cmd -> cmd.getArgument(0))
				.flatMap(Argument::getString)
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
				new DataArgument().setLong(value));
	}

	public LoRaBandwidth getBandwidth() {
		return sendDataGetter("BW")
				.getInteger()
				.map(LoRaBandwidth::valueOf)
				.orElseThrow();
	}

	public int getSpreadingFactor() {
		return sendDataGetter("SF")
				.getInteger()
				.orElseThrow();
	}

	public int getPower() {
		return sendDataGetter("PWR")
				.getInteger()
				.orElseThrow();
	}

	public LoRaCodingRate getCodingRate() {
		return sendDataGetter("CRT")
				.getInteger()
				.map(LoRaCodingRate::valueOf)
				.orElseThrow();
	}

	public Integer getPreambleLength() {
		return sendDataGetter("PRLEN")
				.getInteger()
				.orElseThrow();
	}

	public Integer getPayloadLength() {
		return sendDataGetter("PYLEN")
				.getInteger()
				.orElseThrow();
	}

	public boolean isEnabledCrc() {
		return sendDataGetter("CRC")
				.getBoolean()
				.orElseThrow();
	}

	public boolean isIqInverted() {
		return sendDataGetter("IIQ")
				.getBoolean()
				.orElseThrow();
	}

	public int getRxSymbolTimeout() {
		return sendDataGetter("STO")
				.getInteger()
				.orElseThrow();
	}

	public int getTxTimeout() {
		return sendDataGetter("TXTO")
				.getInteger()
				.orElseThrow();
	}

	public LoRaMode getMode() {
		return sendDataGetter("MODE")
				.getString()
				.map(LoRaMode::valueOf)
				.orElseThrow();
	}

	public LoRaConfiguration getConfiguration() {
		Command tx = CommandFactory.createGetter("RTO");
		Command rx = CommandFactory.createSetter("RTO", new DataArgument(), new DataArgument());
		Command cmd = port.send(tx, rx, 3).orElseThrow();
		Integer minDelta = cmd.getArgument(0).getInteger().orElseThrow();
		Integer maxDelta = cmd.getArgument(1).getInteger().orElseThrow();


		return new LoRaConfiguration()
				.setMode(getMode())
				.setFrequency(getFrequency())
				.setBandwidth(getBandwidth())
				.setPower(getPower())
				.setSpreadingFactory(getSpreadingFactor())
				.setCodingRate(getCodingRate())
				.setEnableCrc(isEnabledCrc())
				.setIqInverted(isIqInverted())
				.setPreambleLength(getPreambleLength())
				.setPayloadLength(getPayloadLength())
				.setTxTimeout(getTxTimeout())
				.setRxSymbolTimeout(getRxSymbolTimeout())
				.setMinDelta(minDelta)
				.setMaxDelta(maxDelta);
	}

	public LoRaPort getPort() {
		return port;
	}
}
