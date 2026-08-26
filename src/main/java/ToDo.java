/**
 * Represents a todo task without any date/time attached.
 */
public class ToDo extends Task{
    /**
     * Initializes an uncompleted todo task with the given description.
     *
     * @param description description of the todo
     */
    public ToDo(String description){
        super(description);
    }

    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }

    @Override
    public String toString(){
        return "[T]" + super.toString();
    }
}
