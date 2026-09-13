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
        assertTrue(listResponse.getMessage().contains("1.[T][ ] revise Java"));
    }

    /** Verifies that invalid input becomes a styled error response. */
    @Test
    public void process_invalidCommand_returnsErrorResponse() {
        CommandResponse response = new CommandProcessor(storagePath()).process("unknown");

        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertTrue(response.getMessage().contains("James hasn't heard of this command"));
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
}
