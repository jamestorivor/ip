import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class James {

    private static final Path FILE_PATH = Path.of("data", "james.txt");
    public static String greeting = "____________________________________________________________\n" +
            "JAMES THE CHATTY CHATBOT\n" +
            "Hello! I'm James.\n" +
            "I can do anything for you!\n" +
            "____________________________________________________________\n";

    public static String exitMessage = "____________________________________________________________\n" +
            "Bye. Rest your eyes!\n" +
            "____________________________________________________________";

    public static String createAddTaskMessage(Task task, int taskCount) {
        return "____________________________________________________________\n" +
                "Got it. I've added this task:\n" + task + "\n" +
                "Now you have %d tasks in the list.\n".formatted(taskCount) +
                "____________________________________________________________";
    }

    public static String markMessage(Task task) {
        return "____________________________________________________________\n" +
                "Nice! I've marked this task as done:\n" +
                task+
                "\n" +
                "____________________________________________________________";
    }

    public static String unmarkMessage(Task task) {
        return "____________________________________________________________\n" +
                "OK, I've marked this task as not done yet:\n" +
                task+
                "\n" +
                "____________________________________________________________";
    }

    public static String deleteMessage(Task task) {
        return "____________________________________________________________\n" +
                "Noted. I've removed this task:\n" + task + "\n" +
                "Now you have %d tasks in the list.\n".formatted(tasks.size()) +
                "____________________________________________________________";
    }

    public static String[] parseCommand(String string) throws UserInputException {
        if (string == null || string.trim().isEmpty()) {
            throw new UserInputException("No command specified\n" + "Try: <command> <arguments:optional>");
        }

        return string.trim().split(" ", 2);
    }

    public static Task parseTodo(String arguments) throws UserInputException{
        if (arguments == null || arguments.trim().isEmpty()){
            throw new UserInputException("The description of a todo cannot be empty.\n"
                    + "Try: todo <description>");
        }
        return new ToDo(arguments.trim());
    }

    public static Task parseEvent(String arguments) throws UserInputException{
        if (arguments == null || arguments.trim().isEmpty()){
            throw new UserInputException("The description of a event cannot be empty.\n"
                    + "Try: event <description> /from <start> /to <end>");
        }
        String[] eventParts = arguments.split(" /from ", 2);
        if (eventParts.length < 2 || eventParts[0].trim().isEmpty()){
            throw new UserInputException(
                    "An event needs a description followed by /from.\n"
                            + "Try: event <description> /from <start> /to <end>");
        }
        String[] timeParts = eventParts[1].split(" /to ", 2);
        if (timeParts.length < 2 || timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty()){
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

    public static Task parseDeadline(String arguments) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()){
            throw new UserInputException("The description of a deadline cannot be empty.\n"
                    + "Try: deadline <description> /by <end-date>");
        }
        String[] deadlineParts = arguments.split(" /by ", 2);
        if (deadlineParts.length < 2 || deadlineParts[0].trim().isEmpty() || deadlineParts[1].trim().isEmpty()){
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
     * Parses and validates the task number supplied to a mark-related command.
     *
     * @param arguments the text after the command
     * @param command the command being processed, either {@code mark} or {@code unmark}
     * @return the zero-based index of the selected task
     * @throws UserInputException if no valid task number is supplied
     */
    public static int parseTaskNumber(String arguments, String command) throws UserInputException {
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

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new UserInputException("James says there is no task number " + taskNumber + ".\n"
                    + "Your list currently has " + tasks.size() + " tasks.");
        }
        return taskNumber - 1;
    }

    public static Task deleteTask(int index) throws UserInputException{
        try {
            return tasks.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new UserInputException("James says there is no task number " + (index + 1) + ".\n"
                    + "Your list currently has " + tasks.size() + " tasks.");
        }
    }

    public static Command parseCommandType(String commandString) throws UserInputException{
        try {
            return Command.valueOf(commandString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserInputException("James hasn't heard of this command :(");
        }
    }

    public static ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Loads tasks from the persistent storage file on disk into memory.
     * If the file does not exist, an empty list is returned.
     * Corrupted lines are skipped gracefully with a warning.
     *
     * @return an ArrayList containing the loaded tasks
     */
    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        File file = FILE_PATH.toFile();
        if (!file.exists()) {
            return loadedTasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (!line.trim().isEmpty()) {
                    try {
                        Task task = Task.fromFileString(line);
                        loadedTasks.add(task);
                    } catch (UserInputException e) {
                        System.out.println("Warning: Skipping invalid saved task entry: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Storage file not found; return empty list
        } catch (Exception e) {
            System.out.println("Warning: Error reading saved tasks file: " + e.getMessage());
        }
        return loadedTasks;
    }

    /**
     * Saves all current tasks to the persistent storage file.
     * Automatically creates any necessary parent directories.
     */
    public static void saveTasks() {
        try {
            File file = FILE_PATH.toFile();
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs() && !parentDir.exists()) {
                    System.out.println("Warning: Unable to create storage directory: " + parentDir.getPath());
                    return;
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(task.toFileString() + System.lineSeparator());
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Checks if a task occurs on a specific date.
     * Deadlines match if their deadline date is the specified date.
     * Events match if the specified date falls within their start and end dates (inclusive).
     *
     * @param task the task to check
     * @param date the date to check against
     * @return true if the task occurs on the specified date
     */
    public static boolean taskDateMatch(Task task, LocalDate date) {
        if (task instanceof Event) {
            Event eventTask = (Event) task;
            LocalDate from = eventTask.getFrom();
            LocalDate to = eventTask.getTo();
            return !date.isBefore(from) && !date.isAfter(to);
        } else if (task instanceof Deadline) {
            Deadline deadlineTask = (Deadline) task;
            LocalDate by = deadlineTask.getBy();
            return date.isEqual(by);
        }
        return false;
    }

    /**
     * Returns a list of tasks that occur on the specified date.
     *
     * @param date the date to filter tasks by
     * @return an ArrayList containing matching tasks
     */
    public static ArrayList<Task> getTasksOnDate(LocalDate date) {
        ArrayList<Task> tasksWithDate = new ArrayList<>();
        for (Task task : tasks) {
            if (taskDateMatch(task, date)) {
                tasksWithDate.add(task);
            }
        }
        return tasksWithDate;
    }

    /**
     * Parses and validates a date string argument into a LocalDate object.
     *
     * @param arguments user-supplied date string
     * @return parsed LocalDate object
     * @throws UserInputException if the date argument is empty or formatted incorrectly
     */
    public static LocalDate parseDate(String arguments) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()){
            throw new UserInputException("Please provide a date in the format: yyyy-mm-dd");
        }
        try {
            return LocalDate.parse(arguments.trim());
        } catch (DateTimeParseException e) {
            throw new UserInputException("The date format provided is incorrect! Please use the format: yyyy-mm-dd");
        }
    }

    public enum Command {
        DELETE, TODO, EVENT, DEADLINE, MARK, UNMARK, LIST, BYE, LIST_BY_DATE
    }


    public static void main(String[] args) {
        tasks = loadTasks();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println(greeting);
        while (running && scanner.hasNextLine()) {
            String input = scanner.nextLine();
            Task newTask = null;

            try {
            String[] parts = parseCommand(input);
            String commandStr = parts[0];
            String arguments = parts.length > 1 ? parts[1] : null;

            Command command = parseCommandType(commandStr);
            switch (command) {
                case LIST_BY_DATE:
                    LocalDate date = parseDate(arguments);
                    ArrayList<Task> taskList = getTasksOnDate(date);
                    System.out.println("____________________________________________________________");
                    String message = "Here are the tasks in your list that matches the date %s:".formatted(date);
                    for (int i = 0; i < taskList.size(); i++){
                        message = message + "\n%d.%s".formatted(i + 1, taskList.get(i));
                    }
                    System.out.println(message);
                    System.out.println("____________________________________________________________");
                    break;
                case DELETE:
                    int delIdx = parseTaskNumber(arguments, "delete");
                    Task delTask = deleteTask(delIdx);
                    saveTasks();
                    System.out.println(deleteMessage(delTask));
                    break;
                case TODO:
                    newTask = parseTodo(arguments);
                    break;
                case EVENT:
                    newTask = parseEvent(arguments);
                    break;
                case DEADLINE:
                    newTask = parseDeadline(arguments);
                    break;
                case MARK:
                    int markIdx = parseTaskNumber(arguments, "mark");
                    Task taskToMark = tasks.get(markIdx);
                    taskToMark.markDone();
                    saveTasks();
                    System.out.println(markMessage(taskToMark));
                    break;
                case UNMARK:
                    int unmarkIdx = parseTaskNumber(arguments, "unmark");
                    Task taskToUnmark = tasks.get(unmarkIdx);
                    taskToUnmark.markNotDone();
                    saveTasks();
                    System.out.println(unmarkMessage(taskToUnmark));
                    break;
                case BYE:
                    running = false;
                    break;
                case LIST:
                    System.out.println("____________________________________________________________");
                    String output = "Here are the tasks in your list:";
                    for (int i = 0; i < tasks.size(); i++){
                        output = output + "\n%d.%s".formatted(i + 1,tasks.get(i));
                    }
                    System.out.println(output);
                    System.out.println("____________________________________________________________");
                    break;
                default:
                    throw new UserInputException("James hasn't heard of this command :(");
            }
            } catch (UserInputException e){
                System.out.println("____________________________________________________________\n" +
                "OH NO James Doesnt Know What To Do!!!\n" + e.getMessage() +
                "\n____________________________________________________________");
            }
            if (newTask != null) {
                tasks.add(newTask);
                saveTasks();
                System.out.println(createAddTaskMessage(newTask,tasks.size()));
            }
        }
        System.out.println(exitMessage);
        scanner.close();
    }
}
