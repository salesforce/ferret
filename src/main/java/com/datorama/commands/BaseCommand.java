package com.datorama.commands;

import com.datorama.services.OutputService;
import org.jboss.logmanager.Level;
import picocli.CommandLine;

import java.util.logging.Logger;

public class BaseCommand {
    @CommandLine.Option(names = {"-X", "--debug"}, scope = CommandLine.ScopeType.INHERIT, hidden = true)
    public void setDebug(boolean isDebugEnabled) {
        if (isDebugEnabled) {
            Logger.getLogger("com.datorama").setLevel(Level.DEBUG);
            OutputService.getInstance().normal("Print logs with debug level or higher.");
        }
    }
}
