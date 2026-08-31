package james.task;

import java.time.LocalDate;

/**
 * Represents an event task occurring during a specific time period.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Initializes an uncompleted event task with the given description, start time, and end time.
     *
     * @param description description of the event
     * @param from start date LocalDate
     * @param to end date LocalDate
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of the event.
     *
     * @return start date
     */
    public LocalDate getFrom() {
        return this.from;
    }

    /**
     * Returns the end date of the event.
     *
     * @return end date
     */
    public LocalDate getTo() {
        return this.to;
    }

    /**
     * Returns the string representation of the event task formatted for file storage.
     *
     * @return formatted event string for storage
     */
    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + this.from + " | " + this.to;
    }

    /**
     * Returns the string representation of the event task for display.
     *
     * @return formatted display string of the event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(
                this.from.format(DISPLAY_FORMAT),
                this.to.format(DISPLAY_FORMAT));
    }
}
