/**
 * Represents an event task occurring during a specific time period.
 */
public class Event extends Task{
    String from;
    String to;

    /**
     * Initializes an uncompleted event task with the given description, start time, and end time.
     *
     * @param description description of the event
     * @param from start time/date string
     * @param to end time/date string
     */
    public Event(String description, String from, String to){
        super(description);
        this.from = from != null ? from.trim() : "";
        this.to = to != null ? to.trim() : "";
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + this.from + " | " + this.to;
    }

    @Override
    public String toString(){
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(this.from, this.to);
    }
}
