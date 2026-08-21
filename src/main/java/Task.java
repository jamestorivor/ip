public class Task {
    public String description;
    public boolean done;

    public Task(String description){
        this.description = description;
        this.done = false;
    }

    public void markDone(){
        this.done = true;
        System.out.println(
                "____________________________________________________________" +
                        "Nice! I've marked this task as done:\n" +
                        this +
                        "\n" +
                "____________________________________________________________"
        );
    }

    public void markNotDone(){
        this.done = false;
        System.out.println(
                "____________________________________________________________" +
                        "OK, I've marked this task as not done yet:\n" +
                        this +
                        "\n" +
                "____________________________________________________________"
        );
    }

    public String getMark(){
        return (this.done ? "[X]":"[ ]");
    }

    @Override
    public String toString(){
        return this.getMark() + " " +  this.description;
    }
}
