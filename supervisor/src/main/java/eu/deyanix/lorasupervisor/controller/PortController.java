package eu.deyanix.lorasupervisor.controller;

import eu.deyanix.lorasupervisor.model.LoRaSerialPort;
import eu.deyanix.lorasupervisor.service.LoRaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Port")
@RestController
public class PortController {
	private final LoRaService loRaService;

	public PortController(LoRaService loRaService) {
		this.loRaService = loRaService;
	}

	@GetMapping("/ports")
	public List<LoRaSerialPort> getPorts() {
		return loRaService.getPorts();
	}

	@PostMapping("/ports/{port}/connect")
	public LoRaSerialPort connect(@PathVariable String port) {
		return loRaService.connect(port);
	}

	@PostMapping("/ports/{port}/disconnect")
	public void disconnect(@PathVariable String port) {
		loRaService.disconnect(port);
	}
}
