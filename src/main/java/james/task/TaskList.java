package james.task;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Encapsulates the in-memory list of tasks and provides operations to
 * add, delete, query, and filter tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs a TaskList initialized with an existing list of tasks.
     *
     * @param tasks the initial list of tasks, or {@code null} for an empty list
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks == null ? new ArrayList<>() : tasks;
    }

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Retrieves the task at the specified zero-based index.
     *
     * @param index zero-based index of the task
     * @return the task at the specified index
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a new task to the task list.
     *
     * @param task the task to add
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task to delete
     * @return the removed task
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public Task deleteTask(int index) throws IndexOutOfBoundsException {
        return tasks.remove(index);
    }

    /**
     * Deletes a specific task from the list.
     *
     * @param task the task to remove
     */
    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return the list of tasks
     */
    public ArrayList<Task> getTasks() {
        return this.tasks;
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
        if (task instanceof Event eventTask) {
            LocalDate from = eventTask.getFrom();
            LocalDate to = eventTask.getTo();
            return !date.isBefore(from) && !date.isAfter(to);
        } else if (task instanceof Deadline deadlineTask) {
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
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        ArrayList<Task> tasksWithDate = new ArrayList<>();
        for (Task task : tasks) {
            if (taskDateMatch(task, date)) {
                tasksWithDate.add(task);
            }
        }
        return tasksWithDate;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            message.append("%d.%s\n".formatted(i + 1, tasks.get(i)));
        }
        return message.toString();
    }
}
