package ua.nanit.limbo.server;

import ua.nanit.limbo.server.command.*;

import java.util.*;

public final class CommandManager extends Thread {

    private final Map<String, Command> commands = new HashMap<>();

    public Map<String, Command> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public void register(Command cmd, String... aliases) {
        for (String alias : aliases) {
            commands.put(alias.toLowerCase(), cmd);
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            try {
                command = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                break;
            }

            Command handler = getCommand(command);

            if (handler != null) {
                try {
                    handler.execute();
                } catch (Throwable t) {
                    Log.error("Cannot execute command:", t);
                }
                continue;
            }

            Log.info("Unknown command. Type \"help\" to get commands list");
        }
    }

    public void registerAll(LimboServer server) {
        register(new HelpCommand(server), "help");
        register(new ConnectionCommand(server), "conn");
        register(new MemoryCommand(server), "memory");
        register(new StopCommand(), "stop");
    }
}
