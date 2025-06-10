package eu.deyanix.lorasupervisor.protocol.command;

import eu.deyanix.lorasupervisor.protocol.buffer.BufferWriter;
import eu.deyanix.lorasupervisor.protocol.buffer.BufferReader;

public interface Argument {
	boolean read(BufferReader buffer);
	boolean write(BufferWriter buffer);
}
