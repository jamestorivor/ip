package james.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import james.task.Deadline;
import james.task.Event;
import james.task.Task;
import james.task.TaskList;
import james.task.ToDo;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that load returns an empty ArrayList when the storage file does not exist.
     */
    @Test
    public void load_nonExistentFile_returnsEmptyList() {
        Path nonExistentPath = tempDir.resolve("non_existent_data.txt");
        Storage storage = new Storage(nonExistentPath.toString());

        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    /**
     * Tests that load correctly parses tasks of each type from an existing valid storage file.
     */
    @Test
    public void load_validFileWithTasks_returnsParsedTaskList() throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        List<String> lines = List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2026-06-06",
                "E | 0 | orientation | 2026-08-06 | 2026-08-08"
        );
        Files.write(filePath, lines);

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertInstanceOf(ToDo.class, tasks.get(0));
        assertEquals("read book", tasks.get(0).getDescription());

        assertInstanceOf(Deadline.class, tasks.get(1));
        Deadline deadline = (Deadline) tasks.get(1);
        assertEquals("return book", deadline.getDescription());
        assertTrue(deadline.isDone());
        assertEquals(LocalDate.parse("2026-06-06"), deadline.getBy());

        assertInstanceOf(Event.class, tasks.get(2));
        Event event = (Event) tasks.get(2);
        assertEquals("orientation", event.getDescription());
        assertEquals(LocalDate.parse("2026-08-06"), event.getFrom());
        assertEquals(LocalDate.parse("2026-08-08"), event.getTo());
    }

    /**
     * Tests that corrupted or malformed lines are skipped gracefully while valid lines are loaded.
     */
    @Test
    public void load_fileWithCorruptedLines_skipsCorruptedLines() throws IOException {
        Path filePath = tempDir.resolve("corrupted_tasks.txt");
        List<String> lines = List.of(
                "T | 0 | valid todo",
                "INVALID | LINE | FORMAT",
                "D | 0 | bad deadline | not-a-date",
                "T | 1 | another valid todo"
        );
        Files.write(filePath, lines);

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("valid todo", tasks.get(0).getDescription());
        assertEquals("another valid todo", tasks.get(1).getDescription());
    }

    /**
     * Tests that save serializes all tasks in the task list into the storage file in the expected format.
     */
    @Test
    public void save_taskListWithTasks_writesAllTasksToFile() throws IOException {
        Path filePath = tempDir.resolve("saved_tasks.txt");
        Storage storage = new Storage(filePath.toString());

        TaskList taskList = new TaskList();
        ToDo todo = new ToDo("buy milk");
        Deadline deadline = new Deadline("submit essay", LocalDate.parse("2026-12-15"));
        deadline.markDone();
        taskList.addTask(todo);
        taskList.addTask(deadline);

        storage.save(taskList);

        assertTrue(Files.exists(filePath));
        List<String> savedLines = Files.readAllLines(filePath);
        assertEquals(2, savedLines.size());
        assertEquals("T | 0 | buy milk", savedLines.get(0));
        assertEquals("D | 1 | submit essay | 2026-12-15", savedLines.get(1));
    }

    /**
     * Tests that save creates missing parent directories when saving to a nested path.
     */
    @Test
    public void save_nestedNonExistentDirectory_createsDirectoriesAndSaves() throws IOException {
        Path nestedPath = tempDir.resolve("nested").resolve("subfolder").resolve("tasks.txt");
        Storage storage = new Storage(nestedPath.toString());

        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("nested task"));

        storage.save(taskList);

        assertTrue(Files.exists(nestedPath));
        List<String> savedLines = Files.readAllLines(nestedPath);
        assertEquals(1, savedLines.size());
        assertEquals("T | 0 | nested task", savedLines.get(0));
    }
}
