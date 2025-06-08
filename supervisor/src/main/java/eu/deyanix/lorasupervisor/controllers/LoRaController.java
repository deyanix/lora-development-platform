package eu.deyanix.lorasupervisor.controllers;

import com.fazecast.jSerialComm.SerialPort;
import eu.deyanix.lorasupervisor.protocol.LoRaNode;
import eu.deyanix.lorasupervisor.protocol.LoRaNodeProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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
	public List<LoRaNode> getNodes() {
		return nodeProvider.getNodes();
	}

	@PostMapping("/nodes/detect")
	public void detect() {
		nodeProvider.detect();
	}
}
