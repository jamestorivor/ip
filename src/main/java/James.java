import java.util.Scanner;

public class James {
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

    public static String[] parseCommand(String string) throws UserInputException {
        if (string == null || string.trim().isEmpty()) {
            throw new UserInputException("No command specified\n" + "Try: <command> <arguments:optional>");
        }

        return string.split(" ",2);
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
        return new Event(eventParts[0].trim(), timeParts[0].trim(), timeParts[1].trim());

    }

    public static Task parseDeadline(String arguments) throws UserInputException {
        if (arguments == null || arguments.trim().isEmpty()){
            throw new UserInputException("The description of a deadline cannot be empty.\n"
                    + "Try: deadline <description> /by <end-date>");
        }
        String[] deadlineParts = arguments.split(" /by ", 2);
        if (deadlineParts.length < 2 || deadlineParts[1].trim().isEmpty()){
            throw new UserInputException("A deadline needs a by date.\n" + "Try: deadline <description> /by <date>");
        }
        return new Deadline(deadlineParts[0], deadlineParts[1]);
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

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new UserInputException("James says there is no task number " + taskNumber + ".\n"
                    + "Your list currently has " + taskCount + " tasks.");
        }
        return taskNumber - 1;
    }

    public static final int MAX_ENTRIES = 100;
    public static Task[] tasks = new Task[MAX_ENTRIES];
    public static int taskCount = 0;


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println(greeting);
        while (running && scanner.hasNextLine()) {
            String input = scanner.nextLine();
            Task newTask = null;

            try {
            String[] parts = parseCommand(input);
            String command = parts[0];
            String arguments = parts.length > 1 ? parts[1] : null;

            switch (command) {
            case "todo":
                newTask = parseTodo(arguments);
                break;
            case "event":
                newTask = parseEvent(arguments);
                break;
            case "deadline":
                newTask = parseDeadline(arguments);
                break;
            case "mark":
                int markIdx = parseTaskNumber(arguments, "mark");
                Task taskToMark = tasks[markIdx];
                taskToMark.markDone();
                System.out.println(
                        "____________________________________________________________\n" +
                                "Nice! I've marked this task as done:\n" +
                                 taskToMark+
                                "\n" +
                        "____________________________________________________________"
                );
                break;
            case "unmark":
                int unmarkIdx = parseTaskNumber(arguments, "unmark");
                Task taskToUnmark = tasks[unmarkIdx];
                taskToUnmark.markNotDone();
                System.out.println(
                        "____________________________________________________________\n" +
                                "OK, I've marked this task as not done yet:\n" +
                                taskToUnmark+
                                "\n" +
                        "____________________________________________________________"
                );
                break;
            case "bye":
                running = false;
                break;
            case "list":
                System.out.println("____________________________________________________________");
                String output = "Here are the tasks in your list:";
                for (int i = 0; i < taskCount; i++){
                    output = output + "\n%d.%s".formatted(i + 1,tasks[i]);
                }
                System.out.println(output);
                System.out.println("____________________________________________________________");
                break;
            default:
                throw new UserInputException("James hasn't head of this command :(");
            }
            } catch (UserInputException e){
                System.out.println("____________________________________________________________");
                System.out.println("OH NO James Doesnt Know What To Do!!!\n" + e.getMessage());
                System.out.println("____________________________________________________________");
            }
            if (newTask != null) {
                tasks[taskCount] = newTask;
                taskCount++;
                System.out.println(createAddTaskMessage(newTask,taskCount));
            }
        }
        System.out.println(exitMessage);
        scanner.close();
    }
}
