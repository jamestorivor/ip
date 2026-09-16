package james.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import james.task.Deadline;
import james.task.Event;
import james.task.Task;
import james.task.TaskList;
import james.task.ToDo;

/**
 * Verifies recovery, replacement, and compatibility of saved tasks.
 */
public class StorageRecoveryTest {
    @TempDir
    private Path directory;

    @Test
    public void load_repairedFile_clearsWarningsAndAllowsSave() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken record\n");
        Storage storage = new Storage(file.toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.hasLoadErrors());
        assertFalse(storage.save(new TaskList()));
        Files.writeString(file, "T | 0 | repaired\n");
        TaskList tasks = new TaskList(storage.load());
        assertEquals("repaired", tasks.getTask(0).getDescription());
        assertFalse(storage.hasLoadErrors());
        assertEquals("", storage.getLoadWarning());
        tasks.addTask(new ToDo("new"));
        assertTrue(storage.save(tasks));
        assertEquals(2, storage.load().size());
    }

    @Test
    public void save_smallerThenEmptyList_replacesPreviousContents() {
        Storage storage = new Storage(directory.resolve("tasks.txt").toString());
        TaskList tasks = new TaskList();
        tasks.addTask(new ToDo("first"));
        tasks.addTask(new ToDo("second"));
        assertTrue(storage.save(tasks));
        tasks.deleteTask(0);
        assertTrue(storage.save(tasks));
        assertEquals(List.of("second"), storage.load().stream().map(Task::getDescription).toList());
        assertTrue(storage.save(new TaskList()));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_unicodeMixedTasks_roundTripsAllDetailsAndStates() {
        Storage storage = new Storage(directory.resolve("tasks.txt").toString());
        LocalDate start = LocalDate.of(2028, 2, 29);
        TaskList tasks = new TaskList();
        for (boolean isDone : new boolean[]{false, true}) {
            List<Task> group = List.of(new ToDo("读书 📚 " + isDone),
                    new Deadline("报告 " + isDone, start), new Event("旅行 " + isDone, start, start.plusDays(2)));
            for (Task task : group) {
                if (isDone) {
                    task.markDone();
                }
                tasks.addTask(task);
            }
        }
        assertTrue(storage.save(tasks));
        List<Task> loaded = storage.load();
        assertEquals(tasks.getSize(), loaded.size());
        assertFalse(storage.hasLoadErrors());
        for (int i = 0; i < loaded.size(); i++) {
            Task expected = tasks.getTask(i);
            Task actual = loaded.get(i);
            assertEquals(expected.getClass(), actual.getClass());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.isDone(), actual.isDone());
            assertEquals(expected.toFileString(), actual.toFileString());
        }
    }

    @Test
    public void load_legacyTodoWithDelimiter_survivesSaveAndReload() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 1 | safe | legacy\n");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList(storage.load());
        tasks.addTask(new ToDo("new"));
        assertTrue(storage.save(tasks));
        Task legacy = storage.load().get(0);
        assertEquals("safe | legacy", legacy.getDescription());
        assertTrue(legacy.isDone());
    }

    @Test
    public void load_blankLinesAndCrLf_preservesOrderWithoutWarnings() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "\r\n  \r\nT | 0 | first\r\n\r\nT | 1 | last");
        Storage storage = new Storage(file.toString());
        assertEquals(List.of("first", "last"), storage.load().stream().map(Task::getDescription).toList());
        assertFalse(storage.hasLoadErrors());
        assertEquals("", storage.getLoadWarning());
    }

    @Test
    public void load_duplicateEachType_blocksSaveAndPreservesBytes() throws IOException {
        String[] records = {"T | 0 | read", "D | 0 | report | 2026-09-16",
            "E | 0 | trip | 2026-09-16 | 2026-09-18"};
        Path file = directory.resolve("tasks.txt");
        for (String record : records) {
            Files.writeString(file, record + "\n" + record.replace(" | 0 | ", " | 1 | ") + "\n");
            byte[] original = Files.readAllBytes(file);
            Storage storage = new Storage(file.toString());
            assertEquals(1, storage.load().size(), record);
            assertTrue(storage.hasLoadErrors(), record);
            assertTrue(storage.getLoadWarning().contains("line 2"));
            assertFalse(storage.save(new TaskList()));
            assertArrayEquals(original, Files.readAllBytes(file));
        }
    }
}
