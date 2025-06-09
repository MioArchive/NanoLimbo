package ua.nanit.limbo.server.command;

import ua.nanit.limbo.server.Command;

public class StopCommand implements Command {

    @Override
    public void execute() {
        System.exit(0);
    }

    @Override
    public String description() {
        return "Shuts down the server and terminates all active connections";
    }

}
