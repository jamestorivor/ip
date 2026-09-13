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

    private static final String DONE_MARK = "[X]";
    private static final String NOT_DONE_MARK = "[ ]";
    private static final String STORAGE_TASK_FORMAT = "%d | %s";
    private static final String EMPTY_STORAGE_LINE_MESSAGE = "Storage line cannot be empty.";
    private static final String STORAGE_FIELD_PATTERN = " \\| ";
    private static final String CORRUPTED_TASK_MESSAGE_PREFIX = "Corrupted task line in storage: ";
    private static final String NOT_DONE_STORAGE_VALUE = "0";
    private static final String DONE_STORAGE_VALUE = "1";
    private static final String INVALID_STATUS_MESSAGE_PREFIX = "Invalid completion status in storage: ";
    private static final String TODO_STORAGE_TYPE = "T";
    private static final String EMPTY_TODO_DESCRIPTION_MESSAGE =
            "Todo description cannot be empty in storage.";
    private static final String DEADLINE_STORAGE_TYPE = "D";
    private static final String CORRUPTED_DEADLINE_MESSAGE_PREFIX = "Corrupted deadline line in storage: ";
    private static final String EMPTY_DEADLINE_DESCRIPTION_MESSAGE =
            "Deadline description cannot be empty in storage.";
    private static final String EMPTY_DEADLINE_DATE_MESSAGE = "Deadline date cannot be empty in storage.";
    private static final String CORRUPTED_DEADLINE_DATE_MESSAGE_PREFIX =
            "Corrupted deadline date in storage: ";
    private static final String EVENT_STORAGE_TYPE = "E";
    private static final String CORRUPTED_EVENT_MESSAGE_PREFIX = "Corrupted event line in storage: ";
    private static final String EMPTY_EVENT_DESCRIPTION_MESSAGE =
            "Event description cannot be empty in storage.";
    private static final String EMPTY_EVENT_TIMES_MESSAGE = "Event times cannot be empty in storage.";
    private static final String CORRUPTED_EVENT_DATE_MESSAGE_PREFIX = "Corrupt event date in storage from:";
    private static final String EVENT_END_DATE_LABEL = " to: ";
    private static final String UNKNOWN_TASK_TYPE_MESSAGE_PREFIX = "Unknown task type in storage: ";

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
        return isDone ? DONE_MARK : NOT_DONE_MARK;
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
        return STORAGE_TASK_FORMAT.formatted(isDone ? 1 : 0, description);
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
            throw new UserInputException(EMPTY_STORAGE_LINE_MESSAGE);
        }

        String[] initialParts = line.split(STORAGE_FIELD_PATTERN, 3);
        if (initialParts.length < 3) {
            throw new UserInputException(CORRUPTED_TASK_MESSAGE_PREFIX + line);
        }

        String type = initialParts[0].trim();
        String doneStr = initialParts[1].trim();
        if (!doneStr.equals(NOT_DONE_STORAGE_VALUE) && !doneStr.equals(DONE_STORAGE_VALUE)) {
            throw new UserInputException(INVALID_STATUS_MESSAGE_PREFIX + doneStr);
        }
        boolean isTaskDone = doneStr.equals(DONE_STORAGE_VALUE);

        Task task;
        switch (type) {
        case TODO_STORAGE_TYPE:
            String[] todoParts = line.split(STORAGE_FIELD_PATTERN, 3);
            String todoDesc = todoParts[2].trim();
            if (todoDesc.isEmpty()) {
                throw new UserInputException(EMPTY_TODO_DESCRIPTION_MESSAGE);
            }
            task = new ToDo(todoDesc);
            break;
        case DEADLINE_STORAGE_TYPE:
            String[] deadlineParts = line.split(STORAGE_FIELD_PATTERN, 4);
            if (deadlineParts.length < 4) {
                throw new UserInputException(CORRUPTED_DEADLINE_MESSAGE_PREFIX + line);
            }
            String deadlineDesc = deadlineParts[2].trim();
            String byStr = deadlineParts[3].trim();
            if (deadlineDesc.isEmpty()) {
                throw new UserInputException(EMPTY_DEADLINE_DESCRIPTION_MESSAGE);
            }
            if (byStr.isEmpty()) {
                throw new UserInputException(EMPTY_DEADLINE_DATE_MESSAGE);
            }
            try {
                LocalDate by = LocalDate.parse(byStr);
                task = new Deadline(deadlineDesc, by);
            } catch (DateTimeParseException e) {
                throw new UserInputException(CORRUPTED_DEADLINE_DATE_MESSAGE_PREFIX + byStr);
            }
            break;
        case EVENT_STORAGE_TYPE:
            String[] eventParts = line.split(STORAGE_FIELD_PATTERN, 5);
            if (eventParts.length < 5) {
                throw new UserInputException(CORRUPTED_EVENT_MESSAGE_PREFIX + line);
            }
            String eventDesc = eventParts[2].trim();
            String fromStr = eventParts[3].trim();
            String toStr = eventParts[4].trim();
            if (eventDesc.isEmpty()) {
                throw new UserInputException(EMPTY_EVENT_DESCRIPTION_MESSAGE);
            }
            if (fromStr.isEmpty() || toStr.isEmpty()) {
                throw new UserInputException(EMPTY_EVENT_TIMES_MESSAGE);
            }
            try {
                LocalDate from = LocalDate.parse(fromStr);
                LocalDate to = LocalDate.parse(toStr);
                task = new Event(eventDesc, from, to);
            } catch (DateTimeParseException e) {
                throw new UserInputException(CORRUPTED_EVENT_DATE_MESSAGE_PREFIX + fromStr
                        + EVENT_END_DATE_LABEL + toStr);
            }
            break;
        default:
            throw new UserInputException(UNKNOWN_TASK_TYPE_MESSAGE_PREFIX + type);
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
