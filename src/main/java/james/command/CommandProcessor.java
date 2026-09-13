package james.command;

import java.time.LocalDate;
import java.util.ArrayList;

import james.exception.UserInputException;
import james.parser.Parser;
import james.storage.Storage;
import james.task.Task;
import james.task.TaskList;

/**
 * Processes commands independently of the console or graphical interface.
 */
public class CommandProcessor {
    private static final String DELETE_COMMAND_STRING = "delete";
    private static final String MARK_COMPLETE_MESSAGE_PREFIX = "Nice! I've marked this task as done:\n";
    private static final String TASK_LIST_ENTRY_FORMAT = "\n%d.%s";
    private static final String LINE_BREAK = "\n";

    private final Storage storage;
    private final TaskList taskList;

    /**
     * Initializes a processor with tasks loaded from the given file.
     *
     * @param filePath Persistent storage file path.
     */
    public CommandProcessor(String filePath) {
        storage = new Storage(filePath);
        taskList = new TaskList(storage.load());
    }

    /**
     * Processes one command and returns its displayable result.
     *
     * @param input Raw command input.
     * @return Result containing message, category, and exit state.
     */
    public CommandResponse process(String input) {
        String EXCEPTION_MESSAGE = "OH NO James Doesnt Know What To Do!!!\n";

        try {
            String[] parts = Parser.parseCommand(input);
            Command command = Parser.parseCommandType(parts[0]);
            String remainingArguments = Parser.extractArguments(parts);

            return executeCommand(command, remainingArguments);
        } catch (UserInputException e) {
            return new CommandResponse(EXCEPTION_MESSAGE + e.getMessage(),
                    CommandResponse.Type.ERROR, false);
        }
    }

    private CommandResponse executeCommand(Command command,String arguments) throws UserInputException{
        String UNKNOWN_COMMAND_MESSAGE = "James hasn't heard of this command :(";

        return switch (command) {
            case LIST_BY_DATE -> listByDate(arguments);
            case FIND -> find(arguments);
            case DELETE -> delete(arguments);
            case TODO -> add(Parser.parseTodo(arguments));
            case EVENT -> add(Parser.parseEvent(arguments));
            case DEADLINE -> add(Parser.parseDeadline(arguments));
            case MARK -> mark(arguments);
            case UNMARK -> unmark(arguments);
            case LIST -> listTasks();
            case BYE -> exit();
            default -> throw new UserInputException(UNKNOWN_COMMAND_MESSAGE);
        };
    }


    private CommandResponse listByDate(String arguments) throws UserInputException {
        LocalDate date = Parser.parseDate(arguments);
        return normal(tasksOnDate(date));
    }


    private CommandResponse find(String arguments) throws UserInputException {
        return normal(matchingTasks(Parser.parseFindKeyword(arguments)));
    }

    private CommandResponse delete(String arguments) throws UserInputException {
        String TASK_REMOVED_PREFIX_MESSAGE = "Noted. I've removed this task:\n";
        String ALL_TASKS_MESSAGE = "\nNow you have %d tasks in the list.\n";

        Task deleted = taskList.deleteTask(Parser.parseTaskNumber(arguments, DELETE_COMMAND_STRING, taskList.size()));
        storage.save(taskList);
        return new CommandResponse(TASK_REMOVED_PREFIX_MESSAGE + deleted + ALL_TASKS_MESSAGE.formatted(taskList.size()),
                CommandResponse.Type.DELETE, false);
    }

    private CommandResponse mark(String arguments) throws UserInputException {
        String MARK_COMMAND_STRING = "mark";

        Task marked = taskList.getTask(Parser.parseTaskNumber(arguments, MARK_COMMAND_STRING, taskList.size()));
        marked.markDone();
        storage.save(taskList);
        return new CommandResponse(MARK_COMPLETE_MESSAGE_PREFIX + marked,
                CommandResponse.Type.MARK, false);
    }

    private CommandResponse exit() {
        String EXIT_MESSAGE = "Bye. Rest your eyes!\n";

        return new CommandResponse(EXIT_MESSAGE, CommandResponse.Type.NORMAL, true);
    }

    private CommandResponse listTasks() {
        String LIST_TASK_MESSAGE = "Here are the tasks in your list:\n";

        return normal(LIST_TASK_MESSAGE + taskList);
    }


    private CommandResponse unmark(String arguments) throws UserInputException {
        String UNMARK_COMMAND_STRING = "unmark";
        String UNMARK_COMPLETE_MESSAGE_PREFIX = "OK, I've marked this task as not done yet:\n";

        Task unmarked = taskList.getTask(Parser.parseTaskNumber(arguments, UNMARK_COMMAND_STRING, taskList.size()));
        unmarked.markNotDone();
        storage.save(taskList);
        return new CommandResponse(UNMARK_COMPLETE_MESSAGE_PREFIX + unmarked,
                CommandResponse.Type.MARK, false);
    }


    private CommandResponse add(Task task) {
        String TASK_ADDED_MESSAGE_PREFIX = "Got it. I've added this task:\n";
        String NUMBER_OF_TASK_MESSAGE = "\nNow you have %d tasks in the list.";

        taskList.addTask(task);
        storage.save(taskList);
        return new CommandResponse(TASK_ADDED_MESSAGE_PREFIX + task + NUMBER_OF_TASK_MESSAGE.formatted(taskList.size()),
                CommandResponse.Type.ADD, false);
    }

    private String tasksOnDate(LocalDate date) {
        String TASKS_MATCHING_DATE_MESSAGE = "Here are the tasks in your list that matches the date %s:".formatted(date);

        ArrayList<Task> tasks = taskList.getTasksOnDate(date);
        StringBuilder message = new StringBuilder(TASKS_MATCHING_DATE_MESSAGE);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, tasks.get(i)));
        }
        return message.append(LINE_BREAK).toString();
    }

    private String matchingTasks(String keyword) {
        String MATCHING_TASKS_MESSAGE = "Here are the matching tasks in your list:";

        ArrayList<Task> tasks = taskList.findTasks(keyword);
        StringBuilder message = new StringBuilder(MATCHING_TASKS_MESSAGE);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, tasks.get(i)));
        }
        return message.toString();
    }

    private CommandResponse normal(String message) {
        return new CommandResponse(message, CommandResponse.Type.NORMAL, false);
    }
}
