package ua.nanit.limbo.server.command;

import ua.nanit.limbo.server.Command;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

public class ConnectionCommand implements Command {

    private final LimboServer server;

    public ConnectionCommand(LimboServer server) {
        this.server = server;
    }

    @Override
    public void execute() {
        Log.info("Connections: %d", server.getConnections().getCount());
    }

    @Override
    public String description() {
        return "Shows the current number of active server connections";
    }
}
