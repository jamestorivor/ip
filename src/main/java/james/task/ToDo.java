package james.task;

/**
 * Represents a todo task without any date/time attached.
 */
public class ToDo extends Task {

    /**
     * Initializes an uncompleted todo task with the given description.
     *
     * @param description description of the todo
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of the todo task formatted for file storage.
     *
     * @return formatted todo string for storage
     */
    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }

    /**
     * Returns the string representation of the todo task for display.
     *
     * @return formatted display string of the todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
