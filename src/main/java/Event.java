public class Event extends Task{
    String from;
    String to;
    public Event(String description, String from, String to){
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString(){
        return "[E]" + super.toString() + " (from: %s to: %s)".formatted(this.from, this.to);
    }
}
