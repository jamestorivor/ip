package james;

import java.time.LocalDate;
import java.util.ArrayList;
import james.command.Command;
import james.exception.UserInputException;
import james.parser.Parser;
import james.storage.Storage;
import james.task.Task;
import james.task.TaskList;
import james.ui.Ui;

/**
 * Main application class that orchestrates interactions between
 * Ui, Storage, Parser, and TaskList components.
 */
public class James {

    private final Storage storage;
    private final TaskList taskList;
    private final Ui ui;

    /**
     * Initializes the chatbot application with the given storage file path.
     *
     * @param filePath Path to the persistent storage file.
     */
    public James(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.taskList = new TaskList(storage.load());
    }

    /**
     * Starts the main command processing loop.
     */
    public void run() {
        boolean isRunning = true;

        ui.greet();
        while (isRunning && ui.hasNextCommand()) {
            String input = ui.readCommand();
            Task newTask = null;

            try {
                String[] parts = Parser.parseCommand(input);
                String commandStr = parts[0];
                String arguments = parts.length > 1 ? parts[1] : null;

                Command command = Parser.parseCommandType(commandStr);
                switch (command) {
                case LIST_BY_DATE:
                    LocalDate date = Parser.parseDate(arguments);
                    ArrayList<Task> taskListByDate = taskList.getTasksOnDate(date);
                    ui.showTasksOnDate(date, taskListByDate);
                    break;
                case DELETE:
                    int delIdx = Parser.parseTaskNumber(arguments, "delete", taskList.size());
                    Task delTask = taskList.deleteTask(delIdx);
                    storage.save(taskList);
                    ui.deleteMessage(delTask, taskList.size());
                    break;
                case TODO:
                    newTask = Parser.parseTodo(arguments);
                    break;
                case EVENT:
                    newTask = Parser.parseEvent(arguments);
                    break;
                case DEADLINE:
                    newTask = Parser.parseDeadline(arguments);
                    break;
                case MARK:
                    int markIdx = Parser.parseTaskNumber(arguments, "mark", taskList.size());
                    Task taskToMark = taskList.getTask(markIdx);
                    taskToMark.markDone();
                    storage.save(taskList);
                    ui.markMessage(taskToMark);
                    break;
                case UNMARK:
                    int unmarkIdx = Parser.parseTaskNumber(arguments, "unmark", taskList.size());
                    Task taskToUnmark = taskList.getTask(unmarkIdx);
                    taskToUnmark.markNotDone();
                    storage.save(taskList);
                    ui.unmarkMessage(taskToUnmark);
                    break;
                case BYE:
                    isRunning = false;
                    break;
                case LIST:
                    ui.showTaskList(taskList);
                    break;
                default:
                    throw new UserInputException("James hasn't heard of this command :(");
                }
            } catch (UserInputException e) {
                ui.showError(e.getMessage());
            }
            if (newTask != null) {
                taskList.addTask(newTask);
                storage.save(taskList);
                ui.createAddTaskMessage(newTask, taskList.size());
            }
        }
        ui.sayBye();
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
