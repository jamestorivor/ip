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
                "added:%s \n".formatted(task) +
                "____________________________________________________________";
    }

    public static final int MAX_LIST_ENTRIES = 100;
    public static String[] tasks = new String[MAX_LIST_ENTRIES];
    public static int taskCount = 0;
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println(greeting);
        while (running && scanner.hasNextLine()) {
            String input = scanner.nextLine();

            switch (input) {
            case "bye":
                running = false;
                break;
            case "tasks":
                System.out.println("____________________________________________________________");
                for (int i = 0; i < taskCount; i++){
                    System.out.println("%d. %s".formatted(i + 1, tasks[i]));
                }
                System.out.println("____________________________________________________________");
                break;
            default:
                String message = createAddTaskMessage(input);
                tasks[taskCount] = input;
                taskCount++;
                System.out.println(message);
                break;
            }
        }
        System.out.println(exitMessage);
        scanner.close();
    }
}
