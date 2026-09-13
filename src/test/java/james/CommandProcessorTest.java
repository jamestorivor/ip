package james;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import james.command.CommandProcessor;
import james.command.CommandResponse;

/** Tests command processing across the shared application backend. */
public class CommandProcessorTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that adding and listing a task updates the shared state. */
    @Test
    public void process_addThenList_returnsUpdatedTaskList() {
        CommandProcessor processor = new CommandProcessor(storagePath());

        CommandResponse addResponse = processor.process("todo revise Java");
        CommandResponse listResponse = processor.process("list");

        assertEquals(CommandResponse.Type.ADD, addResponse.getType());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] revise Java\n", listResponse.getMessage());
    }

    /** Verifies that invalid input becomes a styled error response. */
    @Test
    public void process_invalidCommand_returnsErrorResponse() {
        CommandResponse response = new CommandProcessor(storagePath()).process("unknown");

        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertEquals("OH NO James Doesnt Know What To Do!!!\nJames hasn't heard of this command :(",
                response.getMessage());
        assertFalse(response.isExit());
    }

    /** Verifies that mutations persist when a new processor is created. */
    @Test
    public void process_addThenReload_persistsTask() {
        String storagePath = storagePath();
        new CommandProcessor(storagePath).process("deadline submit report /by 2026-09-07");

        CommandResponse response = new CommandProcessor(storagePath).process("list");

        assertTrue(response.getMessage().contains("submit report"));
    }

    /** Verifies that the bye command requests application termination. */
    @Test
    public void process_bye_returnsExitResponse() {
        CommandResponse response = new CommandProcessor(storagePath()).process("bye");

        assertTrue(response.isExit());
        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertEquals("Bye. Rest your eyes!\n", response.getMessage());
    }

    private String storagePath() {
        return temporaryDirectory.resolve("james.txt").toString();
    }

    /**
     * Tests that mutation confirmations report the size after the operation.
     */
    @Test
    public void process_addThenDelete_reportsUpdatedTaskCounts() {
        CommandProcessor processor = new CommandProcessor(storagePath());

        CommandResponse added = processor.process("todo revise Java");
        CommandResponse deleted = processor.process("delete 1");

        assertEquals("Got it. I've added this task:\n[T][ ] revise Java"
                + "\nNow you have 1 tasks in the list.", added.getMessage());
        assertEquals("Noted. I've removed this task:\n[T][ ] revise Java"
                + "\nNow you have 0 tasks in the list.\n", deleted.getMessage());
    }

    @Test
    public void process_markTask_returnsCompletedTaskMessage() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo buy bread");

        CommandResponse response = processor.process("mark 1");

        assertEquals(CommandResponse.Type.MARK, response.getType());
        assertEquals("Nice! I've marked this task as done:\n[T][X] buy bread", response.getMessage());
    }

    @Test
    public void process_unmarkTask_returnsIncompleteTaskMessage() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo buy bread");
        processor.process("mark 1");

        CommandResponse response = processor.process("unmark 1");

        assertEquals(CommandResponse.Type.MARK, response.getType());
        assertEquals("OK, I've marked this task as not done yet:\n[T][ ] buy bread", response.getMessage());
    }

    @Test
    public void process_findMatchingTasks_returnsNumberedMatches() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo buy bread");
        processor.process("todo read book");
        processor.process("deadline return book /by 2026-06-06");

        CommandResponse response = processor.process("find BOOK");

        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book"
                + "\n2.[D][ ] return book (by: Jun 06 2026)", response.getMessage());
    }

    @Test
    public void process_findWithoutMatches_returnsHeadingOnly() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo read book");

        CommandResponse response = processor.process("find magazine");

        assertEquals("Here are the matching tasks in your list:", response.getMessage());
    }

    @Test
    public void process_listByDateWithMatches_returnsNumberedMatches() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo read book");
        processor.process("deadline return book /by 2026-10-15");
        processor.process("event conference /from 2026-10-14 /to 2026-10-16");

        CommandResponse response = processor.process("list_by_date 2026-10-15");

        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertEquals("Here are the tasks in your list that matches the date 2026-10-15:"
                + "\n1.[D][ ] return book (by: Oct 15 2026)"
                + "\n2.[E][ ] conference (from: Oct 14 2026 to: Oct 16 2026)\n", response.getMessage());
    }

    @Test
    public void process_listByDateWithoutMatches_returnsHeadingOnly() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("deadline return book /by 2026-10-15");

        CommandResponse response = processor.process("list_by_date 2026-10-16");

        assertEquals("Here are the tasks in your list that matches the date 2026-10-16:\n", response.getMessage());
    }
}
