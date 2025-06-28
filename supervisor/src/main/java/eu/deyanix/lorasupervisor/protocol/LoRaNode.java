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

	public LoRaNode setRadioConfiguration(LoRaRadioConfiguration conf) {
		if (commander != null) {
			commander.setRadioConfiguration(conf, radioConfiguration);
			radioConfiguration = commander.getRadioConfiguration();
		} else {
			if (radioConfiguration == null) {
				radioConfiguration = new LoRaRadioConfiguration();
			}

			LoRaCommander.mergeRadioConfiguration(radioConfiguration, conf);
		}
		return this;
	}

	public LoRaConfiguration getConfiguration() {
		return configuration;
	}

	public LoRaNode setConfiguration(LoRaConfiguration conf) {
		if (commander != null) {
			commander.setConfiguration(conf, configuration);
			configuration = commander.getConfiguration();
		} else {
			if (configuration == null) {
				configuration = new LoRaConfiguration();
			}

			LoRaCommander.mergeConfiguration(configuration, conf);
		}
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
