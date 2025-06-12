package eu.deyanix.lorasupervisor.protocol.command;

import java.util.Arrays;

public class CommandFactory {
	public static Command createSetterArgs(String name, Argument... arguments) {
		return new Command(name, false, arguments);
	}

	public static Command createSetter(String name, ArgumentData... arguments) {
		return new Command(name, false, Arrays.stream(arguments)
				.map(Argument::new)
				.toArray(Argument[]::new));
	}

	public static Command createGetterArgs(String name, Argument... arguments) {
		return new Command(name, true, arguments);
	}

	public static Command createGetter(String name, ArgumentData... arguments) {
		return new Command(name, true, Arrays.stream(arguments)
				.map(Argument::new)
				.toArray(Argument[]::new));
	}
}
