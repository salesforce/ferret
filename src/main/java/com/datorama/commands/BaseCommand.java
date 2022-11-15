package com.datorama.commands;

import com.datorama.services.OutputService;
import org.jboss.logmanager.Level;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

@ApplicationScoped
public class BaseCommand {
    @Inject
    OutputService outputService;
    @CommandLine.Option(names = {"-X", "--debug"}, scope = CommandLine.ScopeType.INHERIT, hidden = true)
    public void setDebug(boolean isDebugEnabled) {
        if (isDebugEnabled) {
            Logger.getLogger("com.datorama").setLevel(Level.DEBUG);
            outputService.normal("Print logs with debug level or higher.");
        }
    }
}
