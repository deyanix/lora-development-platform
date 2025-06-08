package eu.deyanix.lorasupervisor;

import eu.deyanix.lorasupervisor.protocol.LoRaNodeProvider;
import eu.deyanix.lorasupervisor.protocol.buffer.LoRaBuffer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoRaSupervisorApplication {

	public static void main(String[] args) {
		LoRaBuffer buffer = new LoRaBuffer();
		buffer.append("RX=");
		buffer.append("DONE,1");
		buffer.append(",1,6,A");
		buffer.append("B\n\n\nC\n");
		buffer.append("TOA=1234\nSTA=IDLE");
		System.out.println(buffer.getLines().get(0).getTime());
		System.out.println(buffer.getLines().get(3).getTime());

//		SpringApplication.run(LoRaSupervisorApplication.class, args);
	}

}
