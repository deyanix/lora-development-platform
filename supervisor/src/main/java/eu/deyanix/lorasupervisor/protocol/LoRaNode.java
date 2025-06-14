package eu.deyanix.lorasupervisor.protocol;

import eu.deyanix.lorasupervisor.model.LoRaNodeState;
import eu.deyanix.lorasupervisor.protocol.config.LoRaAutoConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaMode;
import eu.deyanix.lorasupervisor.protocol.config.LoRaRadioConfiguration;
import eu.deyanix.lorasupervisor.protocol.port.LoRaCommander;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

import java.util.Optional;

public class LoRaNode {
	private final String id;
	private LoRaCommander commander;
	private LoRaMode mode;
	private LoRaRadioConfiguration radioConfiguration;
	private LoRaAutoConfiguration autoConfiguration;

	private boolean flashing;

	public LoRaNode(String id, LoRaCommander commander) {
		this.id = id;
		this.commander = commander;
	}

	public LoRaNode(String id, LoRaPort port) {
		this(id, port.createCommander());
	}

	public String getId() {
		return id;
	}

	public Optional<LoRaPort> getPort() {
		if (commander == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(commander.getPort());
	}

	public LoRaNode setPort(LoRaPort port) {
		if (port == null) {
			this.commander = null;
		} else {
			this.commander = port.createCommander();
		}
		return this;
	}

	public Optional<LoRaCommander> getCommander() {
		return Optional.ofNullable(commander);
	}

	public LoRaNode setCommander(LoRaCommander commander) {
		this.commander = commander;
		return this;
	}

	public LoRaMode getMode() {
		return mode;
	}

	public LoRaNode setMode(LoRaMode mode) {
		this.mode = mode;
		return this;
	}

	public LoRaRadioConfiguration getRadioConfiguration() {
		return radioConfiguration;
	}

	public LoRaNode setRadioConfiguration(LoRaRadioConfiguration radioConfiguration) {
		this.radioConfiguration = radioConfiguration;
		return this;
	}

	public LoRaAutoConfiguration getAutoConfiguration() {
		return autoConfiguration;
	}

	public LoRaNode setAutoConfiguration(LoRaAutoConfiguration autoConfiguration) {
		this.autoConfiguration = autoConfiguration;
		return this;
	}

	public boolean isFlashing() {
		return flashing;
	}

	public void setFlashing(boolean flashing) {
		this.flashing = flashing;
	}

	public void toggleFlashing() {
		this.flashing = !this.flashing;
		getCommander().ifPresent(cmd -> cmd.setLed(this.flashing));
	}

	public boolean isConnected() {
		return commander != null && commander.getPort().getSerialPort().isOpen();
	}

	public LoRaNodeState createState() {
		return new LoRaNodeState()
				.setId(id)
				.setPortName(getPort()
						.map(cmd -> cmd.getSerialPort().getSystemPortName())
						.orElse(null))
				.setMode(mode)
				.setRadioConfiguration(radioConfiguration)
				.setAutoConfiguration(autoConfiguration)
				.setFlashing(flashing)
				.setConnected(isConnected());
	}
}
