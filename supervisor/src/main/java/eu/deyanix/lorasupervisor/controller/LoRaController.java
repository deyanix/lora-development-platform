package eu.deyanix.lorasupervisor.controller;

import eu.deyanix.lorasupervisor.protocol.LoRaNodeProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
				.map(n -> n.getPort().getSerialPort().getSystemPortName())
				.collect(Collectors.toList());
	}

//	@PostMapping("/nodes/detect")
//	public void detect() {
//		nodeProvider.detect();
//	}

//	@GetMapping("/nodes/{id}/buffer")
//	public String getBuffer(@PathVariable String id) {
//		return nodeProvider.getNode(id)
//				.map(n -> n.getPort().getBuffer().read(1))
//				.orElse(null);
//	}

//	@PostMapping("/nodes/{id}/write")
//	public void write(@PathVariable String id) {
//		nodeProvider.getNode(id)
//				.ifPresent(n -> n.getPort().send("+ID?"));
//	}
}
