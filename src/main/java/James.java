import java.util.Scanner;

public class James {
    public static String greeting = "____________________________________________________________\n" +
            "JAMES THE CHATTY CHATBOT\n" +
            "Hello! I'm James.\n" +
            "I can do anything for you!\n" +
            "____________________________________________________________\n";

    public static String exitMessage = "____________________________________________________________\n" +
            "Bye. Rest your eyes!\n" +
            "____________________________________________________________";

    public static String createAddTaskMessage(String task) {
        return "____________________________________________________________\n" +
                "added:" + task + "\n" +
                "____________________________________________________________";
    }

    public static final int MAX_ENTRIES = 100;
    public static Task[] tasks = new Task[MAX_ENTRIES];
    public static int taskCount = 0;


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println(greeting);
        while (running && scanner.hasNextLine()) {
            String input = scanner.nextLine();

            String[] parts = input.split(" ",2);
            String command = parts[0];

            switch (command) {
            case "mark":
                int markIdx = Integer.parseInt(parts[1]);
                Task taskToMark = tasks[markIdx - 1];
                taskToMark.markDone();
                System.out.println(
                        "____________________________________________________________\n" +
                                "Nice! I've marked this task as done:\n" +
                                 taskToMark+
                                "\n" +
                        "____________________________________________________________"
                );
                break;
            case "unmark":
                int unmarkIdx = Integer.parseInt(parts[1]);
                Task taskToUnmark = tasks[unmarkIdx - 1];
                taskToUnmark.markNotDone();
                System.out.println(
                        "____________________________________________________________\n" +
                                "OK, I've marked this task as not done yet:\n" +
                                taskToUnmark+
                                "\n" +
                        "____________________________________________________________"
                );
                break;
            case "bye":
                running = false;
                break;
            case "list":
                System.out.println("____________________________________________________________");
                String output = "Here are the tasks in your list:";
                for (int i = 0; i < taskCount; i++){
                    output = output + "\n%d.%s".formatted(i + 1,tasks[i]);
                }
                System.out.println(output);
                System.out.println("____________________________________________________________");
                break;
            default:
                String message = createAddTaskMessage(input);
                Task newTask = new Task(input);
                tasks[taskCount] = newTask;
                taskCount++;
                System.out.println(message);
                break;
            }
        }
        System.out.println(exitMessage);
        scanner.close();
    }
}
