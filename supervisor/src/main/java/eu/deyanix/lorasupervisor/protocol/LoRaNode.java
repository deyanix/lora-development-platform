package eu.deyanix.lorasupervisor.protocol;

import eu.deyanix.lorasupervisor.model.LoRaNodeState;
import eu.deyanix.lorasupervisor.protocol.config.LoRaConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaRadioConfiguration;
import eu.deyanix.lorasupervisor.protocol.port.LoRaCommander;
import eu.deyanix.lorasupervisor.protocol.port.LoRaPort;

import java.util.Optional;

public class LoRaNode {
	private final String id;
	private LoRaCommander commander;
	private LoRaRadioConfiguration radioConfiguration;
	private LoRaConfiguration configuration;

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

	public LoRaRadioConfiguration getRadioConfiguration() {
		return radioConfiguration;
	}

	public LoRaNode setRadioConfiguration(LoRaRadioConfiguration radioConfiguration) {
		getCommander().ifPresent(cmd -> cmd.setRadioConfiguration(radioConfiguration));
		this.radioConfiguration = commander.getRadioConfiguration();
		return this;
	}

	public LoRaConfiguration getConfiguration() {
		return configuration;
	}

	public LoRaNode setConfiguration(LoRaConfiguration configuration) {
		getCommander().ifPresent(cmd -> cmd.setConfiguration(configuration));
		this.configuration = commander.getConfiguration();
		return this;
	}

	public boolean isFlashing() {
		return flashing;
	}

	public void setFlashing(boolean flashing) {
		getCommander().ifPresent(cmd -> cmd.setLed(flashing));
		this.flashing = flashing;
	}

	public boolean isConnected() {
		return commander != null && commander.getPort().getSerialPort().isOpen();
	}

	public void synchronizeDown() {
		this.radioConfiguration = commander.getRadioConfiguration();
		this.configuration = commander.getConfiguration();
		this.flashing = commander.isLed();
	}

	public void synchronizeUp() {
		commander.setRadioConfiguration(radioConfiguration);
		commander.setConfiguration(configuration);
		commander.setLed(flashing);
	}

	public LoRaNodeState createState() {
		return new LoRaNodeState()
				.setId(id)
				.setPortName(getPort()
						.map(cmd -> cmd.getSerialPort().getSystemPortName())
						.orElse(null))
				.setRadioConfiguration(radioConfiguration)
				.setConfiguration(configuration)
				.setFlashing(flashing)
				.setConnected(isConnected());
	}
}
