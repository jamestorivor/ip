package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Verifies independent undo snapshots and duplicate comparisons.
 */
public class TaskSnapshotTest {
    private static final LocalDate START = LocalDate.of(2026, 9, 16);
    private static final LocalDate END = START.plusDays(2);

    @Test
    public void copy_eachTaskType_preservesDetailsAndIndependentStatus() {
        for (Task original : createTasks()) {
            for (boolean isDone : new boolean[]{false, true}) {
                if (isDone) {
                    original.markDone();
                }
                Task copy = original.copy();
                assertNotSame(original, copy);
                assertEquals(original.getClass(), copy.getClass());
                assertEquals(original.toFileString(), copy.toFileString());
                assertEquals(isDone, copy.isDone());
                copy.markDone();
                assertEquals(isDone, original.isDone());
                copy.markNotDone();
                assertEquals(isDone, original.isDone());
                original.markDone();
                assertFalse(copy.isDone());
            }
        }
    }

    @Test
    public void copy_mixedTaskList_preservesOrderAndIndependentTasks() {
        TaskList original = new TaskList();
        createTasks().forEach(original::addTask);
        original.getTask(1).markDone();
        TaskList copy = original.copy();
        assertNotSame(original.getTasks(), copy.getTasks());
        for (int i = 0; i < original.getSize(); i++) {
            assertNotSame(original.getTask(i), copy.getTask(i));
            assertEquals(original.getTask(i).toFileString(), copy.getTask(i).toFileString());
        }
        copy.getTask(1).markNotDone();
        assertTrue(original.getTask(1).isDone());
        original.getTask(0).markDone();
        assertFalse(copy.getTask(0).isDone());
        copy.deleteTask(0);
        assertEquals(4, original.getSize());
        original.addTask(new ToDo("extra"));
        assertEquals(3, copy.getSize());
    }

    @Test
    public void copy_emptyTaskList_returnsIndependentEmptyList() {
        TaskList original = new TaskList();
        TaskList copy = original.copy();
        assertEquals(0, copy.getSize());
        copy.addTask(new ToDo("new"));
        assertEquals(0, original.getSize());
    }

    @Test
    public void hasSameDetails_differentStatus_returnsTrueWithoutMutation() {
        for (Task original : createTasks()) {
            Task completed = original.copy();
            completed.markDone();
            assertTrue(original.hasSameDetails(completed));
            assertTrue(completed.hasSameDetails(original));
            assertFalse(original.isDone());
            assertTrue(completed.isDone());
        }
    }

    @Test
    public void hasSameDetails_differentFields_returnsFalse() {
        List<Task> distinct = List.of(new Task("same"), new ToDo("same"), new ToDo("different"),
                new Deadline("same", START), new Deadline("same", END),
                new Event("same", START, END), new Event("same", START.plusDays(1), END),
                new Event("same", START, END.plusDays(1)));
        for (int i = 0; i < distinct.size(); i++) {
            for (int j = 0; j < distinct.size(); j++) {
                assertEquals(i == j, distinct.get(i).hasSameDetails(distinct.get(j)), i + " vs " + j);
            }
        }
    }

    /**
     * Creates every supported snapshot type with distinct details.
     */
    private List<Task> createTasks() {
        return List.of(new Task("generic"), new ToDo("read"), new Deadline("report", START),
                new Event("trip", START, END));
    }
}
