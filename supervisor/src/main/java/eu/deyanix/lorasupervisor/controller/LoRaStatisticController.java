package eu.deyanix.lorasupervisor.controller;

import eu.deyanix.lorasupervisor.model.LoRaEventStatistic;
import eu.deyanix.lorasupervisor.model.LoRaMessageDto;
import eu.deyanix.lorasupervisor.service.LoRaStatisticService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "LoRaStatistic")
@RestController
public class LoRaStatisticController {
	private final LoRaStatisticService loRaStatisticService;

	public LoRaStatisticController(LoRaStatisticService loRaStatisticService) {
		this.loRaStatisticService = loRaStatisticService;
	}

	@GetMapping("/nodes/any/messages")
	public List<LoRaMessageDto> getMessages() {
		return loRaStatisticService.getMessages();
	}

	@GetMapping("/nodes/{id}/messages")
	public List<LoRaMessageDto> getMessagesBySenderId(@PathVariable String id) {
		return loRaStatisticService.getMessagesBySenderId(id);
	}

	@GetMapping("/nodes/any/events/statistics")
	public List<LoRaEventStatistic> getEventStatistics() {
		return loRaStatisticService.getEventStatistics();
	}

	@PostMapping("/nodes/any/reset")
	public void reset() {
		loRaStatisticService.reset();
	}
}
