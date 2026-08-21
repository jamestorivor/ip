import java.util.Scanner;

public class James {
    public static void main(String[] args) {
        String greeting = "____________________________________________________________\n" +
                "JAMES THE CHATTY CHATBOT\n" +
                "Hello! I'm James.\n" +
                "I can do anything for you!\n" +
                "____________________________________________________________\n";
        String exitMessage = "____________________________________________________________\n" +
                "Bye. Rest your eyes!\n" +
                "____________________________________________________________";

        Scanner scanner = new Scanner(System.in);

        System.out.println(greeting);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                break;
            }

            System.out.println("    ____________________________________________________________");
            System.out.println("     " + input);
            System.out.println("    ____________________________________________________________");
        }
        System.out.println(exitMessage);
        scanner.close();
    }
}
