package com.datorama;

import com.datorama.commands.*;
import com.datorama.common.Artwork;
import com.datorama.services.OutputService;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@TopCommand
@Command(name = "ferret", description = "Ferret - Pipeline for your local environment setup",
        mixinStandardHelpOptions = true, version = { "Ferret version 2.0.1",
        "Picocli " + CommandLine.VERSION,
        "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
        "OS: ${os.name} ${os.version} ${os.arch}" },
        subcommands = { CommandLine.HelpCommand.class, SetupCommand.class,
                InitializeCommand.class, CredentialsCommand.class, RepositoryCommand.class,
                PipelineCommand.class,PropertiesCommand.class,UserCommand.class,
                TeardownCommand.class,ArgumentsCommand.class}
)
@ApplicationScoped
public class FerretCommand extends BaseCommand implements Runnable {

    @Inject
    OutputService outputService;

    @Override
    public void run() {
        CommandLine commandLine = new CommandLine(new FerretCommand());
        outputService.normal(Artwork.PIPELINE_ARTWORK.getValue());
        commandLine.usage(System.out);
    }

}
