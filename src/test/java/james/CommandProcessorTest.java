package james;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
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

    @Test
    public void process_undoMixedChanges_restoresDetailsAndOrder() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo read");
        processor.process("deadline report /by 2026-09-15");
        processor.process("event meeting /from 2026-09-15 /to 2026-09-16");
        String original = processor.process("list").getMessage();
        processor.process("mark 2");
        String marked = processor.process("list").getMessage();
        processor.process("delete 2");
        processor.process("undo");
        assertEquals(marked, processor.process("list").getMessage());
        processor.process("unmark 2");
        processor.process("undo");
        assertEquals(marked, processor.process("list").getMessage());
        processor.process("undo");
        assertEquals(original, processor.process("list").getMessage());
        processor.process("undo");
        assertFalse(processor.process("list").getMessage().contains("meeting"));
        processor.process("undo");
        assertFalse(processor.process("list").getMessage().contains("report"));
        processor.process("undo");
        assertEquals("Here are the tasks in your list:\n", processor.process("list").getMessage());
        assertEquals("Nothing to undo.", processor.process("undo").getMessage());
    }

    @Test
    public void process_readOnlyInvalidAndNoOpCommands_preservesUndoHistory() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo read");
        processor.process("unmark 1");
        processor.process("mark 1");
        processor.process("mark 1");
        processor.process("find read");
        processor.process("list_by_date 2026-09-15");
        processor.process("delete 99");
        processor.process("todo");
        assertEquals(CommandResponse.Type.ERROR, processor.process("undo 1").getType());
        processor.process("undo");
        assertTrue(processor.process("list").getMessage().contains("[ ] read"));
        processor.process("undo");
        assertEquals("Nothing to undo.", processor.process("undo").getMessage());
    }

    @Test
    public void process_moreThanTwentyChanges_limitsHistoryAndSupportsNewChanges() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        for (int i = 0; i < 21; i++) {
            processor.process("todo task " + i);
        }
        for (int i = 0; i < 20; i++) {
            processor.process("undo");
        }
        assertEquals("Nothing to undo.", processor.process("undo").getMessage());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] task 0\n",
                processor.process("list").getMessage());
        processor.process("todo new task");
        processor.process("undo");
        assertFalse(processor.process("list").getMessage().contains("new task"));
    }

    @Test
    public void process_undoThenRestart_persistsRestorationWithoutHistory() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo read");
        processor.process("mark 1");
        processor.process("undo");
        CommandProcessor restarted = new CommandProcessor(storagePath());
        assertTrue(restarted.process("list").getMessage().contains("[ ] read"));
        assertEquals("Nothing to undo.", restarted.process("undo").getMessage());
    }

    @Test
    public void process_saveFailure_preservesStateAndUndoForRetry() throws IOException {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo read");
        Path destination = Path.of(storagePath());
        Files.delete(destination);
        Files.createDirectory(destination);
        Files.writeString(destination.resolve("blocker"), "prevent replacement");
        assertEquals(CommandResponse.Type.ERROR, processor.process("mark 1").getType());
        assertTrue(processor.process("list").getMessage().contains("[ ] read"));
        assertEquals(CommandResponse.Type.ERROR, processor.process("undo").getType());
        assertTrue(processor.process("list").getMessage().contains("read"));
        Files.delete(destination.resolve("blocker"));
        Files.delete(destination);
        processor.process("undo");
        assertEquals("Here are the tasks in your list:\n", processor.process("list").getMessage());
        assertEquals("Nothing to undo.", processor.process("undo").getMessage());
    }

    private String storagePath() {
        return temporaryDirectory.resolve("james.txt").toString();
    }
}
