package james;

import james.command.CommandProcessor;
import james.command.CommandResponse;
import james.ui.Ui;

/**
 * Main application class that orchestrates interactions between
 * Ui, Storage, Parser, and TaskList components.
 */
public class James {

    private final Ui ui;
    private final CommandProcessor commandProcessor;

    /**
     * Initializes the chatbot application with the given storage file path.
     *
     * @param filePath Path to the persistent storage file.
     */
    public James(String filePath) {
        this.ui = new Ui();
        this.commandProcessor = new CommandProcessor(filePath);
    }

    public CommandResponse getResponse(String input) {
        return commandProcessor.process(input);
    }

    /**
     * Starts the main command processing loop.
     */
    public void run() {
        ui.greet();
        boolean isRunning = true;
        while (isRunning && ui.hasNextCommand()) {
            CommandResponse response = commandProcessor.process(ui.readCommand());
            ui.showResponse(response.getMessage());
            isRunning = !response.isExit();
        }
        ui.close();
    }

    /**
     * Main application entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new James("data/james.txt").run();
    }
}
