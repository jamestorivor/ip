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
    private static final String LINE_BREAK = "\n";
    private static final String GREETING_TITLE = "JAMES THE CHATTY CHATBOT\n";
    private static final String GREETING_INTRODUCTION = "Hello! I'm James.\n";
    private static final String GREETING_OFFER = "I can do anything for you!\n";
    private static final String EXIT_MESSAGE = "Bye. Rest your eyes!\n";
    private static final String TASK_ADDED_MESSAGE_PREFIX = "Got it. I've added this task:\n";
    private static final String TASK_COUNT_MESSAGE_FORMAT = "Now you have %d tasks in the list.\n";
    private static final String ERROR_MESSAGE_PREFIX = "OH NO James Doesnt Know What To Do!!!\n";
    private static final String MARK_COMPLETE_MESSAGE_PREFIX = "Nice! I've marked this task as done:\n";
    private static final String UNMARK_COMPLETE_MESSAGE_PREFIX =
            "OK, I've marked this task as not done yet:\n";
    private static final String TASK_REMOVED_MESSAGE_PREFIX = "Noted. I've removed this task:\n";
    private static final String TASK_LIST_MESSAGE = "Here are the tasks in your list:\n";
    private static final String TASKS_ON_DATE_MESSAGE_FORMAT =
            "Here are the tasks in your list that matches the date %s:";
    private static final String TASK_LIST_ENTRY_FORMAT = "\n%d.%s";
    private static final String MATCHING_TASKS_MESSAGE = "Here are the matching tasks in your list:";
    private static final String DIVIDER = "____________________________________________________________";
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
     * @return The user input string, or {@code null} if no line is available.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Checks if there is another line of input available from the user.
     *
     * @return {@code true} if another line exists, {@code false} otherwise.
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
     * @param message The content string to encase.
     * @return The encased message string.
     */
    public String encaseMessage(String message) {
        return DIVIDER + LINE_BREAK + message + DIVIDER;
    }

    /**
     * Displays the welcome message and greeting banner.
     */
    public void greet() {
        System.out.println(encaseMessage(GREETING_TITLE
                + GREETING_INTRODUCTION
                + GREETING_OFFER) + LINE_BREAK);
    }

    /**
     * Displays the exit message.
     */
    public void sayBye() {
        System.out.println(encaseMessage(EXIT_MESSAGE));
    }

    /**
     * Displays a response from the shared command processor.
     *
     * @param message Response text.
     */
    public void showResponse(String message) {
        boolean hasTrailingLineBreak = message.endsWith(LINE_BREAK);
        String terminatedMessage = hasTrailingLineBreak ? message : message + LINE_BREAK;
        System.out.println(encaseMessage(terminatedMessage));
    }

    /**
     * Displays a confirmation message after a task is added.
     *
     * @param task The task that was added.
     * @param taskCount The updated total number of tasks.
     */
    public void createAddTaskMessage(Task task, int taskCount) {
        System.out.println(encaseMessage(TASK_ADDED_MESSAGE_PREFIX
                + task
                + LINE_BREAK
                + TASK_COUNT_MESSAGE_FORMAT.formatted(taskCount)));
    }

    /**
     * Displays an error message banner with the error details.
     *
     * @param message Details of the error to show.
     */
    public void showError(String message) {
        System.out.println(encaseMessage(ERROR_MESSAGE_PREFIX
                + message + LINE_BREAK));
    }

    /**
     * Displays a confirmation message when a task is marked as done.
     *
     * @param task The marked task.
     */
    public void markMessage(Task task) {
        System.out.println(encaseMessage(MARK_COMPLETE_MESSAGE_PREFIX
                + task + LINE_BREAK));
    }

    /**
     * Displays a confirmation message when a task is marked as not done.
     *
     * @param task The unmarked task.
     */
    public void unmarkMessage(Task task) {
        System.out.println(encaseMessage(UNMARK_COMPLETE_MESSAGE_PREFIX
                + task + LINE_BREAK));
    }

    /**
     * Displays a confirmation message after a task is removed.
     *
     * @param task The removed task.
     * @param taskListSize The updated total number of tasks.
     */
    public void deleteMessage(Task task, int taskListSize) {
        System.out.println(encaseMessage(TASK_REMOVED_MESSAGE_PREFIX + task + LINE_BREAK
                + TASK_COUNT_MESSAGE_FORMAT.formatted(taskListSize)));
    }

    /**
     * Displays all tasks currently in the task list.
     *
     * @param taskList The TaskList to display.
     */
    public void showTaskList(TaskList taskList) {
        System.out.println(encaseMessage(TASK_LIST_MESSAGE
                + taskList));
    }

    /**
     * Displays tasks occurring on a specified date.
     *
     * @param date The date queried.
     * @param taskListByDate The list of tasks matching the date.
     */
    public void showTasksOnDate(LocalDate date, ArrayList<Task> taskListByDate) {
        StringBuilder message = new StringBuilder(
                TASKS_ON_DATE_MESSAGE_FORMAT.formatted(date));
        for (int i = 0; i < taskListByDate.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, taskListByDate.get(i)));
        }
        message.append(LINE_BREAK);
        System.out.println(encaseMessage(message.toString()));
    }

    /**
     * Displays tasks matching a search keyword.
     *
     * @param matchingTasks Tasks that matched the search keyword.
     */
    public void showMatchingTasks(ArrayList<Task> matchingTasks) {
        StringBuilder message = new StringBuilder(MATCHING_TASKS_MESSAGE);
        for (int i = 0; i < matchingTasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, matchingTasks.get(i)));
        }
        message.append(LINE_BREAK);
        System.out.println(encaseMessage(message.toString()));
    }
}
