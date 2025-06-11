package eu.deyanix.lorasupervisor.controller;

import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.LoRaNodeProvider;
import eu.deyanix.lorasupervisor.protocol.config.LoRaConfiguration;
import eu.deyanix.lorasupervisor.protocol.port.LoRaCommander;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "LoRa")
@RestController
public class LoRaController {
	private final LoRaNodeProvider nodeProvider;

	public LoRaController(LoRaNodeProvider nodeProvider) {
		this.nodeProvider = nodeProvider;
	}

	@GetMapping("/nodes")
	public List<String> getNodes() {
		return nodeProvider.getNodes()
				.stream()
				.map(LoRaNode::getId)
				.collect(Collectors.toList());
	}

	@PostMapping("/nodes/detect")
	public void detect() {
		nodeProvider.detect();
	}

	@GetMapping("/nodes/{id}")
	public LoRaConfiguration getConfiguration(@PathVariable String id) {
		return nodeProvider.getNode(id)
				.orElseThrow()
				.getPort()
				.createCommander()
				.getConfiguration();
	}

	@PostMapping("/nodes/{id}")
	public void setConfiguration(@PathVariable String id, @RequestBody LoRaConfiguration configuration) {
		nodeProvider.getNode(id)
				.orElseThrow()
				.getPort()
				.createCommander()
				.setConfiguration(configuration);
	}

	@PostMapping("/nodes/{id}/led")
	public void setLed(@PathVariable String id) {
		LoRaCommander node = nodeProvider.getNode(id)
				.orElseThrow()
				.getPort()
				.createCommander();

		node.setLed(!node.isLed());
	}

	@PostMapping("/nodes/{id}/transmit")
	public void write(@PathVariable String id, @RequestBody String data) {
		nodeProvider.getNode(id)
				.orElseThrow()
				.getPort()
				.createCommander()
				.transmit(data);
	}
}
