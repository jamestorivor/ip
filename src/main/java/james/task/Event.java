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
     * @param description Description of the event.
     * @param from Start date LocalDate.
     * @param to End date LocalDate.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of the event.
     *
     * @return Start date.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date of the event.
     *
     * @return End date.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns the string representation of the event task formatted for file storage.
     *
     * @return Formatted event string for storage.
     */
    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + from + " | " + to;
    }

    /**
     * Returns the string representation of the event task for display.
     *
     * @return Formatted display string of the event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(
                from.format(DISPLAY_FORMAT),
                to.format(DISPLAY_FORMAT));
    }
}
