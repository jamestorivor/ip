/**
 * Represents a generic task with a description and completion status.
 */
public class Task {
    public String description;
    public boolean done;

    /**
     * Initializes an uncompleted task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description){
        this.description = description;
        this.done = false;
    }

    public void markDone(){
        this.done = true;
    }

    public void markNotDone(){
        this.done = false;
    }

    public String getMark(){
        return this.done ? "[X]":"[ ]";
    }

    /**
     * Returns the string representation of the task formatted for file storage.
     *
     * @return formatted task string for storage
     */
    public String toFileString() {
        return "%d | %s".formatted(this.done ? 1 : 0, this.description);
    }

    /**
     * Parses a line from the storage file into a corresponding Task instance.
     *
     * @param line string read from the storage file
     * @return corresponding Task subclass instance
     * @throws UserInputException if the line format is invalid or unknown
     */
    public static Task fromFileString(String line) throws UserInputException {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new UserInputException("Corrupted task line in storage: " + line);
        }
        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        Task task;
        switch (type) {
        case "T":
            task = new ToDo(description);
            break;
        case "D":
            if (parts.length < 4) {
                throw new UserInputException("Corrupted deadline line in storage: " + line);
            }
            task = new Deadline(description, parts[3].trim());
            break;
        case "E":
            if (parts.length < 5) {
                throw new UserInputException("Corrupted event line in storage: " + line);
            }
            task = new Event(description, parts[3].trim(), parts[4].trim());
            break;
        default:
            throw new UserInputException("Unknown task type in storage: " + type);
        }

        if (isDone) {
            task.markDone();
        }
        return task;
    }

    @Override
    public String toString(){
        return this.getMark() + " " +  this.description;
    }
}
