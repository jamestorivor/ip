package james.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import james.task.Deadline;
import james.task.Task;
import james.task.TaskList;
import james.task.ToDo;

/**
 * Unit tests for {@link Ui}.
 */
public class UiTest {

    private final PrintStream standardOut = System.out;
    private final InputStream standardIn = System.in;
    private ByteArrayOutputStream outputStreamCaptor;

    /**
     * Sets up output stream redirection to capture console output before each test.
     */
    @BeforeEach
    public void setUp() {
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    /**
     * Restores the standard System.out and System.in streams after each test.
     */
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setIn(standardIn);
    }

    // ==========================================
    // Formatting & Helper Tests
    // ==========================================

    /**
     * Tests that encaseMessage encloses the given message string between horizontal dividing lines.
     */
    @Test
    public void encaseMessage_validString_returnsMessageEnclosedInDividers() {
        Ui ui = new Ui();
        String result = ui.encaseMessage("Test message\n");
        String expected = "____________________________________________________________\n"
                + "Test message\n"
                + "____________________________________________________________";
        assertEquals(expected, result);
    }

    // ==========================================
    // Console Output Tests
    // ==========================================

    /**
     * Tests that greet prints the welcome banner and introductory message.
     */
    @Test
    public void greet_invoked_printsGreetingBanner() {
        Ui ui = new Ui();
        ui.greet();
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("JAMES THE CHATTY CHATBOT"));
        assertTrue(output.contains("Hello! I'm James."));
    }

    /**
     * Tests that sayBye prints the exit message.
     */
    @Test
    public void sayBye_invoked_printsByeMessage() {
        Ui ui = new Ui();
        ui.sayBye();
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Bye. Rest your eyes!"));
    }

    /**
     * Tests that createAddTaskMessage prints the confirmation of task addition and total count.
     */
    @Test
    public void createAddTaskMessage_validTask_printsAddedTaskAndCount() {
        Ui ui = new Ui();
        ToDo task = new ToDo("buy bread");
        ui.createAddTaskMessage(task, 1);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Got it. I've added this task:"));
        assertTrue(output.contains("[T][ ] buy bread"));
        assertTrue(output.contains("Now you have 1 tasks in the list."));
    }

    /**
     * Tests that showError prints the formatted error banner with the given error message.
     */
    @Test
    public void showError_validErrorMessage_printsErrorBanner() {
        Ui ui = new Ui();
        ui.showError("Invalid input");

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("OH NO James Doesnt Know What To Do!!!"));
        assertTrue(output.contains("Invalid input"));
    }

    /**
     * Tests that markMessage prints the marked-as-done confirmation message.
     */
    @Test
    public void markMessage_validTask_printsMarkConfirmation() {
        Ui ui = new Ui();
        ToDo task = new ToDo("buy bread");
        task.markDone();
        ui.markMessage(task);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Nice! I've marked this task as done:"));
        assertTrue(output.contains("[T][X] buy bread"));
    }

    /**
     * Tests that unmarkMessage prints the marked-as-not-done confirmation message.
     */
    @Test
    public void unmarkMessage_validTask_printsUnmarkConfirmation() {
        Ui ui = new Ui();
        ToDo task = new ToDo("buy bread");
        ui.unmarkMessage(task);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("OK, I've marked this task as not done yet:"));
        assertTrue(output.contains("[T][ ] buy bread"));
    }

    /**
     * Tests that deleteMessage prints the task deletion confirmation and updated count.
     */
    @Test
    public void deleteMessage_validTask_printsRemovalConfirmation() {
        Ui ui = new Ui();
        ToDo task = new ToDo("buy bread");
        ui.deleteMessage(task, 0);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Noted. I've removed this task:"));
        assertTrue(output.contains("[T][ ] buy bread"));
        assertTrue(output.contains("Now you have 0 tasks in the list."));
    }

    /**
     * Tests that showTaskList prints all tasks currently in the task list.
     */
    @Test
    public void showTaskList_validTaskList_printsTasks() {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("task 1"));
        ui.showTaskList(taskList);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Here are the tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] task 1"));
    }

    /**
     * Tests that showTasksOnDate prints tasks matching the specified date.
     */
    @Test
    public void showTasksOnDate_matchingTasks_printsTasksMatchingDate() {
        Ui ui = new Ui();
        LocalDate date = LocalDate.parse("2026-10-15");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline("return book", date));

        ui.showTasksOnDate(date, tasks);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Here are the tasks in your list that matches the date 2026-10-15:"));
        assertTrue(output.contains("1.[D][ ] return book (by: Oct 15 2026)"));
    }

    // ==========================================
    // Input Reading Tests
    // ==========================================

    /**
     * Tests that readCommand correctly reads input from the configured input stream.
     */
    @Test
    public void readCommand_withInput_returnsEnteredString() {
        String inputData = "todo buy apples\n";
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));

        Ui ui = new Ui();
        assertTrue(ui.hasNextCommand());
        assertEquals("todo buy apples", ui.readCommand());
        ui.close();
    }

    /**
     * Tests that hasNextCommand returns false when the input stream is empty.
     */
    @Test
    public void hasNextCommand_emptyInput_returnsFalse() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        Ui ui = new Ui();
        assertFalse(ui.hasNextCommand());
        ui.close();
    }

    /**
     * Tests that showMatchingTasks prints the required heading and numbered results.
     */
    @Test
    public void showMatchingTasks_matchingTasks_printsNumberedResults() {
        Ui ui = new Ui();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", LocalDate.parse("2026-06-06")));

        ui.showMatchingTasks(tasks);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book"));
    }
}
