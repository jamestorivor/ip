package james;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.random.RandomGenerator;

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
    @Test
    public void process_invalidInputs_preservesTasksAndHistory() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("todo keep");
        String[] invalidInputs = {
            "todo keep", "todo bad | description", "todo bad\nrecord", "list extra", "bye extra",
            "deadline report /by 2026-02-30", "deadline report /by 2026-09-16 /by 2026-09-17",
            "event trip /from 2026-09-16 /to 2026-09-16",
            "event trip /from 2026-09-17 /to 2026-09-16",
            "event trip /to 2026-09-17 /from 2026-09-16",
            "event trip /from 2026-09-16 /from 2026-09-17 /to 2026-09-18",
            "mark 99999999999999999999", "delete 1 1", "deadline report /until 2026-09-16"
        };
        for (String input : invalidInputs) {
            CommandResponse response = processor.process(input);
            assertEquals(CommandResponse.Type.ERROR, response.getType(), input);
            assertFalse(response.isExit(), input);
        }
        assertEquals("Here are the tasks in your list:\n1.[T][ ] keep\n",
                processor.process("list").getMessage());
        processor.process("undo");
        assertEquals("Here are the tasks in your list:\n", processor.process("list").getMessage());
    }

    @Test
    public void process_whitespaceBetweenArguments_acceptsCommands() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        assertEquals(CommandResponse.Type.ADD,
                processor.process("  deadline\t report   /by\t2026-09-16  ").getType());
        assertEquals(CommandResponse.Type.ADD,
                processor.process("event\ttrip\t/from  2026-09-16\t/to  2026-09-17").getType());
    }

    @Test
    public void process_duplicateCompletedTask_rejectsButAllowsDifferentDates() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("deadline report /by 2026-09-16");
        processor.process("mark 1");
        assertEquals(CommandResponse.Type.ERROR,
                processor.process("deadline report /by 2026-09-16").getType());
        assertEquals(CommandResponse.Type.ADD,
                processor.process("deadline report /by 2026-09-17").getType());
    }

    @Test
    public void process_corruptedStorage_explainsRecoveryAndPreservesData() throws IOException {
        Path file = Path.of(storagePath());
        Files.writeString(file, "broken record\n");
        CommandProcessor processor = new CommandProcessor(file.toString());
        CommandResponse response = processor.process("todo keep");
        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertTrue(response.getMessage().contains("restart James"));
        assertEquals("broken record\n", Files.readString(file));
        assertEquals("Nothing to undo.", processor.process("undo").getMessage());
    }

    @Test
    public void process_randomStickerControlledIndexes_returnsEveryPackagedSticker() throws IOException {
        String[] names = {"nankore", "otsu", "naisu", "gomen", "yatta"};
        for (int i = 0; i < names.length; i++) {
            final int selectedIndex = i;
            RandomGenerator generator = new RandomGenerator() {
                @Override
                public long nextLong() {
                    throw new AssertionError("Selection should use nextInt(bound)");
                }

                @Override
                public int nextInt(int bound) {
                    assertEquals(names.length, bound);
                    return selectedIndex;
                }
            };
            CommandProcessor processor = new CommandProcessor(storagePath(), generator);
            CommandResponse response = processor.process("  RANDOM_STICKER  ");
            String expectedPath = "/images/" + names[i] + "_pandorobou.png";
            assertEquals(expectedPath, response.getStickerPath());
            assertEquals("Here's a random sticker!", response.getMessage());
            assertEquals(CommandResponse.Type.NORMAL, response.getType());
            assertFalse(response.isExit());
            try (var stream = getClass().getResourceAsStream(expectedPath)) {
                assertNotNull(stream);
                assertNotNull(javax.imageio.ImageIO.read(stream));
            }
            assertEquals(expectedPath, processor.process("random_sticker").getStickerPath());
        }
    }

    @Test
    public void process_randomSticker_preservesTasksStorageAndUndo() throws IOException {
        CommandProcessor processor = new CommandProcessor(storagePath());
        processor.process("random_sticker");
        assertFalse(Files.exists(Path.of(storagePath())));
        assertEquals("Nothing to undo.", processor.process("undo").getMessage());
        processor.process("todo keep");
        String saved = Files.readString(Path.of(storagePath()));
        String tasks = processor.process("list").getMessage();
        processor.process("random_sticker");
        assertEquals(saved, Files.readString(Path.of(storagePath())));
        assertEquals(tasks, processor.process("list").getMessage());
        processor.process("undo");
        assertEquals("Here are the tasks in your list:\n", processor.process("list").getMessage());
    }

    @Test
    public void process_randomStickerExtraArguments_returnsTextError() {
        CommandProcessor processor = new CommandProcessor(storagePath());
        CommandResponse response = processor.process("random_sticker extra");
        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertEquals("OH NO James Doesnt Know What To Do!!!\nRANDOM_STICKER does not take arguments.",
                response.getMessage());
        assertNull(response.getStickerPath());
        assertNull(processor.process("list").getStickerPath());
    }

}
