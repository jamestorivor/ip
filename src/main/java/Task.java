public class Task {
    public String description;
    public boolean done;

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

    @Override
    public String toString(){
        return this.getMark() + " " +  this.description;
    }
}
