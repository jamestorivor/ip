import java.time.LocalDate;

/**
 * Represents an event task occurring during a specific time period.
 */
public class Event extends Task{
    LocalDate from;
    LocalDate to;

    /**
     * Initializes an uncompleted event task with the given description, start time, and end time.
     *
     * @param description description of the event
     * @param from start time/date LocalDate
     * @param to end time/date LocalDate
     */
    public Event(String description, LocalDate from, LocalDate to){
        super(description);
        this.from = from;
        this.to = to;
    }

    public LocalDate getFrom() {
        return this.from;
    }

    public LocalDate getTo() {
        return this.to;
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + this.from + " | " + this.to;
    }

    @Override
    public String toString(){
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(
                this.from.format(DISPLAY_FORMAT),
                this.to.format(DISPLAY_FORMAT));
    }
}
