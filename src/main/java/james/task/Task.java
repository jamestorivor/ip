package james.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import james.exception.UserInputException;

/**
 * Represents a generic task with a description and completion status.
 */
public class Task {
    protected static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected String description;
    protected boolean isDone;

    /**
     * Initializes an uncompleted task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description != null ? description.trim() : "";
        this.isDone = false;
    }

    /**
     * Marks the task as completed.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markNotDone() {
        isDone = false;
    }

    /**
     * Returns the status icon indicating whether the task is done.
     *
     * @return "[X]" if completed, "[ ]" otherwise.
     */
    public String getMark() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return True if completed, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the string representation of the task formatted for file storage.
     *
     * @return Formatted task string for storage.
     */
    public String toFileString() {
        return "%d | %s".formatted(isDone ? 1 : 0, description);
    }

    /**
     * Parses a line from the storage file into a corresponding Task instance.
     * Handles type identification, status validation, and field extraction.
     *
     * @param line String read from the storage file.
     * @return Corresponding Task subclass instance.
     * @throws UserInputException If the line format is invalid, corrupted, or unknown.
     */
    public static Task fromFileString(String line) throws UserInputException {
        if (line == null || line.trim().isEmpty()) {
            throw new UserInputException("Storage line cannot be empty.");
        }

        String[] initialParts = line.split(" \\| ", 3);
        if (initialParts.length < 3) {
            throw new UserInputException("Corrupted task line in storage: " + line);
        }

        String type = initialParts[0].trim();
        String doneStr = initialParts[1].trim();
        if (!doneStr.equals("0") && !doneStr.equals("1")) {
            throw new UserInputException("Invalid completion status in storage: " + doneStr);
        }
        boolean isTaskDone = doneStr.equals("1");

        Task task;
        switch (type) {
        case "T":
            String[] todoParts = line.split(" \\| ", 3);
            String todoDesc = todoParts[2].trim();
            if (todoDesc.isEmpty()) {
                throw new UserInputException("Todo description cannot be empty in storage.");
            }
            task = new ToDo(todoDesc);
            break;
        case "D":
            String[] deadlineParts = line.split(" \\| ", 4);
            if (deadlineParts.length < 4) {
                throw new UserInputException("Corrupted deadline line in storage: " + line);
            }
            String deadlineDesc = deadlineParts[2].trim();
            String byStr = deadlineParts[3].trim();
            if (deadlineDesc.isEmpty()) {
                throw new UserInputException("Deadline description cannot be empty in storage.");
            }
            if (byStr.isEmpty()) {
                throw new UserInputException("Deadline date cannot be empty in storage.");
            }
            try {
                LocalDate by = LocalDate.parse(byStr);
                task = new Deadline(deadlineDesc, by);
            } catch (DateTimeParseException e) {
                throw new UserInputException("Corrupted deadline date in storage: " + byStr);
            }
            break;
        case "E":
            String[] eventParts = line.split(" \\| ", 5);
            if (eventParts.length < 5) {
                throw new UserInputException("Corrupted event line in storage: " + line);
            }
            String eventDesc = eventParts[2].trim();
            String fromStr = eventParts[3].trim();
            String toStr = eventParts[4].trim();
            if (eventDesc.isEmpty()) {
                throw new UserInputException("Event description cannot be empty in storage.");
            }
            if (fromStr.isEmpty() || toStr.isEmpty()) {
                throw new UserInputException("Event times cannot be empty in storage.");
            }
            try {
                LocalDate from = LocalDate.parse(fromStr);
                LocalDate to = LocalDate.parse(toStr);
                task = new Event(eventDesc, from, to);
            } catch (DateTimeParseException e) {
                throw new UserInputException("Corrupt event date in storage from:" + fromStr + " to: " + toStr);
            }
            break;
        default:
            throw new UserInputException("Unknown task type in storage: " + type);
        }

        if (isTaskDone) {
            task.markDone();
        }
        return task;
    }

    /**
     * Returns the string representation of the task including its status icon and description.
     *
     * @return Formatted display string of the task.
     */
    @Override
    public String toString() {
        return getMark() + " " + description;
    }
}
