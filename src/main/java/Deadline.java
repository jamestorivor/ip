import java.time.LocalDate;

/**
 * Represents a task that needs to be completed by a specific deadline.
 */
public class Deadline extends Task{
    LocalDate by;


    /**
     * Initializes an uncompleted deadline task with the given description and due time.
     *
     * @param description description of the deadline task
     * @param by the deadline time/date string
     */
    public Deadline(String description, LocalDate by){
        super(description);
        this.by = by;
    }

    public LocalDate getBy() {
        return this.by;
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + this.by;
    }

    @Override
    public String toString(){
        return "[D]" + super.toString() + " (by: %s)".formatted(this.by.format(DISPLAY_FORMAT));
    }
}
