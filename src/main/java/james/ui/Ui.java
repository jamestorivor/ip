package james.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import james.task.Task;
import james.task.TaskList;

/**
 * Handles user interactions by reading inputs and displaying formatted output.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Constructs a Ui instance and initializes the input scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of input entered by the user.
     *
     * @return the user input string, or {@code null} if no line is available
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Checks if there is another line of input available from the user.
     *
     * @return {@code true} if another line exists, {@code false} otherwise
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Closes the underlying scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Encases a message string within decorative divider lines.
     *
     * @param message the content string to encase
     * @return the encased message string
     */
    public String encaseMessage(String message) {
        return "____________________________________________________________\n" +
                message +
                "____________________________________________________________";
    }

    /**
     * Displays the welcome message and greeting banner.
     */
    public void greet() {
        System.out.println(encaseMessage("JAMES THE CHATTY CHATBOT\n" +
                "Hello! I'm James.\n" +
                "I can do anything for you!\n") + "\n");
    }

    /**
     * Displays the exit message.
     */
    public void sayBye() {
        System.out.println(encaseMessage("Bye. Rest your eyes!\n"));
    }

    /**
     * Displays a confirmation message after a task is added.
     *
     * @param task the task that was added
     * @param taskCount the updated total number of tasks
     */
    public void createAddTaskMessage(Task task, int taskCount) {
        System.out.println(encaseMessage("Got it. I've added this task:\n" +
                task +
                "\n" +
                "Now you have %d tasks in the list.\n".formatted(taskCount)));
    }

    /**
     * Displays an error message banner with the error details.
     *
     * @param message details of the error to show
     */
    public void showError(String message) {
        System.out.println(encaseMessage("OH NO James Doesnt Know What To Do!!!\n" +
                message + "\n"));
    }

    /**
     * Displays a confirmation message when a task is marked as done.
     *
     * @param task the marked task
     */
    public void markMessage(Task task) {
        System.out.println(encaseMessage("Nice! I've marked this task as done:\n" +
                task + "\n"));
    }

    /**
     * Displays a confirmation message when a task is marked as not done.
     *
     * @param task the unmarked task
     */
    public void unmarkMessage(Task task) {
        System.out.println(encaseMessage("OK, I've marked this task as not done yet:\n" +
                task + "\n"));
    }

    /**
     * Displays a confirmation message after a task is removed.
     *
     * @param task the removed task
     * @param taskListSize the updated total number of tasks
     */
    public void deleteMessage(Task task, int taskListSize) {
        System.out.println(encaseMessage("Noted. I've removed this task:\n" + task + "\n" +
                "Now you have %d tasks in the list.\n".formatted(taskListSize)));
    }

    /**
     * Displays all tasks currently in the task list.
     *
     * @param taskList the TaskList to display
     */
    public void showTaskList(TaskList taskList) {
        System.out.println(encaseMessage("Here are the tasks in your list:\n" +
                taskList));
    }

    /**
     * Displays tasks occurring on a specified date.
     *
     * @param date the date queried
     * @param taskListByDate the list of tasks matching the date
     */
    public void showTasksOnDate(LocalDate date, ArrayList<Task> taskListByDate) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list that matches the date %s:".formatted(date));
        for (int i = 0; i < taskListByDate.size(); i++) {
            message.append("\n%d.%s".formatted(i + 1, taskListByDate.get(i)));
        }
        message.append("\n");
        System.out.println(encaseMessage(message.toString()));
    }
}
