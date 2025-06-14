package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.config.LoRaConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaMode;
import eu.deyanix.lorasupervisor.protocol.config.LoRaRadioConfiguration;

public class LoRaNodeState {
	private String id;
	private String portName;
	private LoRaRadioConfiguration radioConfiguration;
	private LoRaConfiguration configuration;
	private boolean flashing;
	private boolean connected;

	public String getId() {
		return id;
	}

	public LoRaNodeState setId(String id) {
		this.id = id;
		return this;
	}

	public String getPortName() {
		return portName;
	}

	public LoRaNodeState setPortName(String portName) {
		this.portName = portName;
		return this;
	}

	public LoRaRadioConfiguration getRadioConfiguration() {
		return radioConfiguration;
	}

	public LoRaNodeState setRadioConfiguration(LoRaRadioConfiguration radioConfiguration) {
		this.radioConfiguration = radioConfiguration;
		return this;
	}

	public LoRaConfiguration getConfiguration() {
		return configuration;
	}

	public LoRaNodeState setConfiguration(LoRaConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}

	public boolean isFlashing() {
		return flashing;
	}

	public LoRaNodeState setFlashing(boolean flashing) {
		this.flashing = flashing;
		return this;
	}

	public boolean isConnected() {
		return connected;
	}

	public LoRaNodeState setConnected(boolean connected) {
		this.connected = connected;
		return this;
	}
}
