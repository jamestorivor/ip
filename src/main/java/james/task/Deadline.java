package james.task;

import java.time.LocalDate;

/**
 * Represents a task that needs to be completed by a specific deadline.
 */
public class Deadline extends Task {
    private final LocalDate by;

    /**
     * Initializes an uncompleted deadline task with the given description and due time.
     *
     * @param description description of the deadline task
     * @param by the deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline date.
     *
     * @return the deadline date
     */
    public LocalDate getBy() {
        return this.by;
    }

    /**
     * Returns the string representation of the deadline task formatted for file storage.
     *
     * @return formatted deadline string for storage
     */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + this.by;
    }

    /**
     * Returns the string representation of the deadline task for display.
     *
     * @return formatted display string of the deadline task
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: %s)".formatted(this.by.format(DISPLAY_FORMAT));
    }
}
