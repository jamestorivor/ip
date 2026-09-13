package james.command;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import james.exception.UserInputException;
import james.parser.Parser;
import james.storage.Storage;
import james.task.Task;
import james.task.TaskList;

/**
 * Processes commands independently of the console or graphical interface.
 */
public class CommandProcessor {
    private static final int MAX_UNDO_CHANGES = 20;

    private static final String DELETE_COMMAND_STRING = "delete";
    private static final String MARK_COMPLETE_MESSAGE_PREFIX = "Nice! I've marked this task as done:\n";
    private static final String TASK_LIST_ENTRY_FORMAT = "\n%d.%s";
    private static final String LINE_BREAK = "\n";

    private final Storage storage;
    private TaskList taskList;
    private final Deque<TaskList> undoSnapshots = new ArrayDeque<>();

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
        String exceptionMessage = "OH NO James Doesnt Know What To Do!!!\n";

        try {
            String[] parts = Parser.parseCommand(input);
            Command command = Parser.parseCommandType(parts[0]);
            String remainingArguments = Parser.extractArguments(parts);

            return executeCommand(command, remainingArguments);
        } catch (UserInputException e) {
            return new CommandResponse(exceptionMessage + e.getMessage(),
                    CommandResponse.Type.ERROR, false);
        }
    }

    /**
     * Dispatches a parsed command to its handler.
     */
    private CommandResponse executeCommand(Command command, String arguments) throws UserInputException {
        String unknownCommandMessage = "James hasn't heard of this command :(";

        return switch (command) {
            case LIST_BY_DATE -> listByDate(arguments);
            case FIND -> find(arguments);
            case UNDO -> undo(arguments);
            case DELETE -> delete(arguments);
            case TODO -> add(Parser.parseTodo(arguments));
            case EVENT -> add(Parser.parseEvent(arguments));
            case DEADLINE -> add(Parser.parseDeadline(arguments));
            case MARK -> mark(arguments);
            case UNMARK -> unmark(arguments);
            case LIST -> listTasks();
            case BYE -> exit();
            default -> throw new UserInputException(unknownCommandMessage);
        };
    }

    /**
     * Lists tasks matching the supplied date.
     */
    private CommandResponse listByDate(String arguments) throws UserInputException {
        LocalDate date = Parser.parseDate(arguments);
        return normal(tasksOnDate(date));
    }

    /**
     * Finds tasks matching the supplied keyword.
     */
    private CommandResponse find(String arguments) throws UserInputException {
        return normal(matchingTasks(Parser.parseFindKeyword(arguments)));
    }

    /**
     * Deletes a task and records its previous state after saving.
     */
    private CommandResponse delete(String arguments) throws UserInputException {
        String taskRemovedPrefixMessage = "Noted. I've removed this task:\n";
        String allTasksMessage = "\nNow you have %d tasks in the list.\n";

        TaskList beforeDelete = taskList.copy();
        Task deleted = taskList.deleteTask(Parser.parseTaskNumber(arguments, DELETE_COMMAND_STRING, taskList.size()));
        saveChange(beforeDelete);
        return new CommandResponse(taskRemovedPrefixMessage + deleted + allTasksMessage.formatted(taskList.size()),
                CommandResponse.Type.DELETE, false);
    }

    /**
     * Marks a task complete and records only an actual status change.
     */
    private CommandResponse mark(String arguments) throws UserInputException {
        String markCommandString = "mark";

        Task marked = taskList.getTask(Parser.parseTaskNumber(arguments, markCommandString, taskList.size()));
        if (!marked.isDone()) {
            TaskList beforeMark = taskList.copy();
            marked.markDone();
            saveChange(beforeMark);
        }
        return new CommandResponse(MARK_COMPLETE_MESSAGE_PREFIX + marked,
                CommandResponse.Type.MARK, false);
    }

    /**
     * Returns the application exit response.
     */
    private CommandResponse exit() {
        String exitMessage = "Bye. Rest your eyes!\n";

        return new CommandResponse(exitMessage, CommandResponse.Type.NORMAL, true);
    }

    /**
     * Returns the current task list.
     */
    private CommandResponse listTasks() {
        String listTaskMessage = "Here are the tasks in your list:\n";

        return normal(listTaskMessage + taskList);
    }

    /**
     * Marks a task incomplete and records only an actual status change.
     */
    private CommandResponse unmark(String arguments) throws UserInputException {
        String unmarkCommandString = "unmark";
        String unmarkCompleteMessagePrefix = "OK, I've marked this task as not done yet:\n";

        Task unmarked = taskList.getTask(Parser.parseTaskNumber(arguments, unmarkCommandString, taskList.size()));
        if (unmarked.isDone()) {
            TaskList beforeUnmark = taskList.copy();
            unmarked.markNotDone();
            saveChange(beforeUnmark);
        }
        return new CommandResponse(unmarkCompleteMessagePrefix + unmarked,
                CommandResponse.Type.MARK, false);
    }

    /**
     * Adds and saves a task while retaining the previous state for undo.
     */
    private CommandResponse add(Task task) throws UserInputException {
        assert task != null : "Parser must return a task";
        TaskList beforeAdd = taskList.copy();
        taskList.addTask(task);
        saveChange(beforeAdd);
        return new CommandResponse("Got it. I've added this task:\n" + task
                + "\nNow you have %d tasks in the list.".formatted(taskList.size()),
                CommandResponse.Type.ADD, false);
    }

    /**
     * Saves a change and records its snapshot, or rolls back on failure.
     */
    private void saveChange(TaskList previous) throws UserInputException {
        if (!storage.save(taskList)) {
            taskList = previous;
            throw new UserInputException("Could not save tasks. No changes were made.");
        }
        undoSnapshots.push(previous);
        if (undoSnapshots.size() > MAX_UNDO_CHANGES) {
            undoSnapshots.removeLast();
        }
    }

    /**
     * Restores and saves the latest snapshot without recording another undo entry.
     */
    private CommandResponse undo(String arguments) throws UserInputException {
        if (arguments != null && !arguments.isBlank()) {
            throw new UserInputException("Undo does not take arguments.\nTry: undo");
        }
        if (undoSnapshots.isEmpty()) {
            return normal("Nothing to undo.");
        }
        TaskList previous = undoSnapshots.peek();
        if (!storage.save(previous)) {
            throw new UserInputException("Could not save tasks. Undo was not applied; try again.");
        }
        taskList = undoSnapshots.pop();
        return normal("Undid the last change.\nNow you have %d tasks in the list.".formatted(taskList.size()));
    }

    /**
     * Formats tasks occurring on the given date.
     */
    private String tasksOnDate(LocalDate date) {
        String tasksMatchingDateMessage = "Here are the tasks in your list that matches the date %s:".formatted(date);

        ArrayList<Task> tasks = taskList.getTasksOnDate(date);
        StringBuilder message = new StringBuilder(tasksMatchingDateMessage);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, tasks.get(i)));
        }
        return message.append(LINE_BREAK).toString();
    }

    /**
     * Formats tasks matching the given keyword.
     */
    private String matchingTasks(String keyword) {
        String matchingTasksMessage = "Here are the matching tasks in your list:";

        ArrayList<Task> tasks = taskList.findTasks(keyword);
        StringBuilder message = new StringBuilder(matchingTasksMessage);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, tasks.get(i)));
        }
        return message.toString();
    }

    /**
     * Wraps a message in a normal response.
     */
    private CommandResponse normal(String message) {
        return new CommandResponse(message, CommandResponse.Type.NORMAL, false);
    }
}
