package james.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import james.command.Command;
import james.exception.UserInputException;
import james.task.Deadline;
import james.task.Event;
import james.task.Task;
import james.task.ToDo;

/**
 * Parses raw user input strings into commands, arguments, task objects, and dates.
 */
public class Parser {

    /**
     * Parses and validates a date string argument into a LocalDate object.
     *
     * @param arguments user-supplied date string
     * @return parsed LocalDate object
     * @throws UserInputException if the date argument is empty or formatted incorrectly
     */
    public static LocalDate parseDate(String arguments) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new UserInputException("Please provide a date in the format: yyyy-mm-dd");
        }
        try {
            return LocalDate.parse(arguments.trim());
        } catch (DateTimeParseException e) {
            throw new UserInputException("The date format provided is incorrect! Please use the format: yyyy-mm-dd");
        }
    }

    /**
     * Parses a command keyword string into its corresponding Command enum.
     *
     * @param commandString command word entered by user
     * @return the matching Command enum constant
     * @throws UserInputException if the command word is unrecognized
     */
    public static Command parseCommandType(String commandString) throws UserInputException {
        try {
            return Command.valueOf(commandString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserInputException("James hasn't heard of this command :(");
        }
    }

    /**
     * Parses and validates the task number supplied to a mark-related or delete command.
     *
     * @param arguments the text after the command
     * @param command the command being processed
     * @param taskListSize the current number of tasks in the list
     * @return the zero-based index of the selected task
     * @throws UserInputException if no valid task number is supplied or index is out of bounds
     */
    public static int parseTaskNumber(String arguments, String command, int taskListSize) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new UserInputException("James asks that you provide a task number.\n"
                    + "Try: " + command + " <task number>");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new UserInputException("James says that the task number must be a whole number.\n"
                    + "Try: " + command + " <task number>");
        }

        if (taskNumber < 1 || taskNumber > taskListSize) {
            throw new UserInputException("James says there is no task number " + taskNumber + ".\n"
                    + "Your list currently has " + taskListSize + " tasks.");
        }
        return taskNumber - 1;
    }

    /**
     * Parses arguments for a deadline command into a Deadline task instance.
     *
     * @param arguments description and /by date arguments
     * @return constructed Deadline task
     * @throws UserInputException if description or date are missing or malformed
     */
    public static Task parseDeadline(String arguments) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new UserInputException("The description of a deadline cannot be empty.\n"
                    + "Try: deadline <description> /by <end-date>");
        }
        String[] deadlineParts = arguments.split(" /by ", 2);
        if (deadlineParts.length < 2 || deadlineParts[0].trim().isEmpty() || deadlineParts[1].trim().isEmpty()) {
            throw new UserInputException("A deadline needs a by date.\n" + "Try: deadline <description> /by <date>");
        }
        try {
            LocalDate by = LocalDate.parse(deadlineParts[1].trim());
            return new Deadline(deadlineParts[0].trim(), by);
        } catch (DateTimeParseException e) {
            throw new UserInputException("Formatting of the date is incorrect, try: yyyy-mm-dd");
        }
    }

    /**
     * Parses arguments for a todo command into a ToDo task instance.
     *
     * @param arguments description of the todo task
     * @return constructed ToDo task
     * @throws UserInputException if description is empty
     */
    public static Task parseTodo(String arguments) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new UserInputException("The description of a todo cannot be empty.\n"
                    + "Try: todo <description>");
        }
        return new ToDo(arguments.trim());
    }

    /**
     * Parses arguments for an event command into an Event task instance.
     *
     * @param arguments description, /from start date, and /to end date
     * @return constructed Event task
     * @throws UserInputException if description or dates are missing or malformed
     */
    public static Task parseEvent(String arguments) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new UserInputException("The description of a event cannot be empty.\n"
                    + "Try: event <description> /from <start> /to <end>");
        }
        String[] eventParts = arguments.split(" /from ", 2);
        if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
            throw new UserInputException(
                    "An event needs a description followed by /from.\n"
                            + "Try: event <description> /from <start> /to <end>");
        }
        String[] timeParts = eventParts[1].split(" /to ", 2);
        if (timeParts.length < 2 || timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty()) {
            throw new UserInputException(
                    "An event needs both a start and end time.\n"
                            + "Try: event <description> /from <start> /to <end>");
        }
        try {
            LocalDate from = LocalDate.parse(timeParts[0].trim());
            LocalDate to = LocalDate.parse(timeParts[1].trim());
            return new Event(eventParts[0].trim(), from, to);
        } catch (DateTimeParseException e) {
            throw new UserInputException("Formatting of the date is incorrect, try: yyyy-mm-dd");
        }
    }

    /**
     * Splits a raw user input line into the command keyword and remaining arguments.
     *
     * @param string raw input line from the user
     * @return an array of strings where index 0 is the command and index 1 (if present) is the arguments
     * @throws UserInputException if the input is null or blank
     */
    public static String[] parseCommand(String string) throws UserInputException {
        if (string == null || string.trim().isEmpty()) {
            throw new UserInputException("No command specified\n" + "Try: <command> <arguments:optional>");
        }

        return string.trim().split(" ", 2);
    }
}
