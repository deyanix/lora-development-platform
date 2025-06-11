package eu.deyanix.lorasupervisor.protocol.command;

public class CommandFactory {
	public static Command createSetter(String name, Argument... arguments) {
		return new Command(name, arguments);
	}

	public static Command createGetter(String name, Argument... arguments) {
		return new Command(name, true, arguments);
	}
}
