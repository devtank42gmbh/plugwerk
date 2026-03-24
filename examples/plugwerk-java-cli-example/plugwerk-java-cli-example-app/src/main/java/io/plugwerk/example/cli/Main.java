package io.plugwerk.example.cli;

import picocli.CommandLine;

/**
 * Entry point for the Plugwerk Java CLI example.
 *
 * <p>Run via Gradle:
 * <pre>
 * ./gradlew :plugwerk-java-cli-example:plugwerk-java-cli-example-app:run \
 *     --args="--server=http://localhost:8080 list"
 * </pre>
 *
 * <p>Or with the fat JAR after {@code ./gradlew assemble}:
 * <pre>
 * java -jar build/libs/plugwerk-java-cli-example-app-*-fat.jar list
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        PlugwerkCli cli = new PlugwerkCli();
        CommandLine commandLine = new CommandLine(cli);
        cli.setCommandLine(commandLine);

        // Pre-parse global options so that pluginsDir, serverUrl etc. are populated
        // from command-line args and env vars before we initialize the plugin manager.
        // Errors (e.g. unknown subcommand before dynamic commands are registered) are
        // intentionally ignored — execute() will handle them properly.
        try {
            commandLine.parseArgs(args);
        } catch (Exception ignored) {}

        // Eagerly initialize the plugin manager so that already-installed plugins are
        // loaded and their CliCommand extensions are registered as picocli subcommands
        // before execute() tries to match the user's subcommand name.
        org.pf4j.PluginManager pm = PluginManagerFactory.create(
                cli.pluginsDir, cli.serverUrl, cli.namespace, cli.accessToken);
        cli.setPluginManager(pm);
        DynamicCommandLoader.loadAll(commandLine, pm);

        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}
