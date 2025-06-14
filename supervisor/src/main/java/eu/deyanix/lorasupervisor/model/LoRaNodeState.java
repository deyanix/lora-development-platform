package eu.deyanix.lorasupervisor.model;

import eu.deyanix.lorasupervisor.protocol.config.LoRaAutoConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaMode;
import eu.deyanix.lorasupervisor.protocol.config.LoRaRadioConfiguration;

public class LoRaNodeState {
	private String id;
	private String portName;
	private LoRaMode mode;
	private LoRaRadioConfiguration radioConfiguration;
	private LoRaAutoConfiguration autoConfiguration;
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

	public LoRaMode getMode() {
		return mode;
	}

	public LoRaNodeState setMode(LoRaMode mode) {
		this.mode = mode;
		return this;
	}

	public LoRaRadioConfiguration getRadioConfiguration() {
		return radioConfiguration;
	}

	public LoRaNodeState setRadioConfiguration(LoRaRadioConfiguration radioConfiguration) {
		this.radioConfiguration = radioConfiguration;
		return this;
	}

	public LoRaAutoConfiguration getAutoConfiguration() {
		return autoConfiguration;
	}

	public LoRaNodeState setAutoConfiguration(LoRaAutoConfiguration autoConfiguration) {
		this.autoConfiguration = autoConfiguration;
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
