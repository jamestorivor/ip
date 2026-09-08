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
        try {
            String[] parts = Parser.parseCommand(input);
            String arguments = parts.length > 1 ? parts[1] : null;
            Command command = Parser.parseCommandType(parts[0]);
            switch (command) {
            case LIST_BY_DATE:
                LocalDate date = Parser.parseDate(arguments);
                return normal(tasksOnDate(date));
            case FIND:
                return normal(matchingTasks(Parser.parseFindKeyword(arguments)));
            case DELETE:
                Task deleted = taskList.deleteTask(Parser.parseTaskNumber(arguments, "delete", taskList.size()));
                storage.save(taskList);
                return new CommandResponse("Noted. I've removed this task:\n" + deleted
                        + "\nNow you have %d tasks in the list.\n".formatted(taskList.size()),
                        CommandResponse.Type.DELETE, false);
            case TODO:
                return add(Parser.parseTodo(arguments));
            case EVENT:
                return add(Parser.parseEvent(arguments));
            case DEADLINE:
                return add(Parser.parseDeadline(arguments));
            case MARK:
                Task marked = taskList.getTask(Parser.parseTaskNumber(arguments, "mark", taskList.size()));
                marked.markDone();
                storage.save(taskList);
                return new CommandResponse("Nice! I've marked this task as done:\n" + marked,
                        CommandResponse.Type.MARK, false);
            case UNMARK:
                Task unmarked = taskList.getTask(Parser.parseTaskNumber(arguments, "unmark", taskList.size()));
                unmarked.markNotDone();
                storage.save(taskList);
                return new CommandResponse("OK, I've marked this task as not done yet:\n" + unmarked,
                        CommandResponse.Type.MARK, false);
            case LIST:
                return normal("Here are the tasks in your list:\n" + taskList);
            case BYE:
                return new CommandResponse("Bye. Rest your eyes!\n", CommandResponse.Type.NORMAL, true);
            default:
                throw new UserInputException("James hasn't heard of this command :(");
            }
        } catch (UserInputException e) {
            return new CommandResponse("OH NO James Doesnt Know What To Do!!!\n" + e.getMessage(),
                    CommandResponse.Type.ERROR, false);
        }
    }

    private CommandResponse add(Task task) {
        taskList.addTask(task);
        storage.save(taskList);
        return new CommandResponse("Got it. I've added this task:\n" + task
                + "\nNow you have %d tasks in the list.".formatted(taskList.size()),
                CommandResponse.Type.ADD, false);
    }

    private String tasksOnDate(LocalDate date) {
        ArrayList<Task> tasks = taskList.getTasksOnDate(date);
        StringBuilder message = new StringBuilder(
                "Here are the tasks in your list that matches the date %s:".formatted(date));
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n%d.%s".formatted(i + 1, tasks.get(i)));
        }
        return message.append("\n").toString();
    }

    private String matchingTasks(String keyword) {
        ArrayList<Task> tasks = taskList.findTasks(keyword);
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n%d.%s".formatted(i + 1, tasks.get(i)));
        }
        return message.toString();
    }

    private CommandResponse normal(String message) {
        return new CommandResponse(message, CommandResponse.Type.NORMAL, false);
    }
}
