package eu.deyanix.lorasupervisor.controller;

import eu.deyanix.lorasupervisor.model.LoRaNodeState;
import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.config.LoRaConfiguration;
import eu.deyanix.lorasupervisor.protocol.config.LoRaRadioConfiguration;
import eu.deyanix.lorasupervisor.service.LoRaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "LoRa")
@RestController
public class LoRaController {
	private final LoRaService loRaService;

	public LoRaController(LoRaService loRaService) {
		this.loRaService = loRaService;
	}

	@GetMapping("/nodes")
	public List<String> getNodes() {
		return loRaService.getNodes()
				.stream()
				.map(LoRaNode::getId)
				.collect(Collectors.toList());
	}

	@GetMapping("/nodes/{id}")
	public LoRaNodeState getState(@PathVariable String id) {
		return loRaService.getNodeById(id)
				.orElseThrow()
				.createState();
	}

	@PatchMapping("/nodes/{id}/radio")
	public void setRadioConfiguration(@PathVariable String id, @RequestBody LoRaRadioConfiguration configuration) {
		loRaService.getNodeById(id)
				.orElseThrow()
				.setRadioConfiguration(configuration);
	}

	@PatchMapping("/nodes/{id}/configuration")
	public void setConfiguration(@PathVariable String id, @RequestBody LoRaConfiguration configuration) {
		loRaService.getNodeById(id)
				.orElseThrow()
				.setConfiguration(configuration);
	}

	@PostMapping("/nodes/{id}/flashing")
	public void toggleFlashing(@PathVariable String id) {
		loRaService.getNodeById(id)
				.orElseThrow()
				.toggleFlashing();
	}

	@PostMapping("/nodes/{id}/transmit")
	public void write(@PathVariable String id, @RequestBody String data) {
		loRaService.getNodeById(id)
				.flatMap(LoRaNode::getCommander)
				.orElseThrow()
				.transmit(data);
	}
}
