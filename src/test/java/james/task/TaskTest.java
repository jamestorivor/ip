package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import james.exception.UserInputException;

/**
 * Unit tests for {@link Task}.
 */
public class TaskTest {

    // ==========================================
    // Constructor & Field Tests
    // ==========================================

    @Test
    public void constructor_nullDescription_setsEmptyDescription() {
        Task task = new Task(null);
        assertEquals("", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void constructor_withWhitespace_trimsDescription() {
        Task task = new Task("  wash dishes  ");
        assertEquals("wash dishes", task.getDescription());
        assertFalse(task.isDone());
    }

    // ==========================================
    // Mark & Unmark Tests
    // ==========================================

    @Test
    public void markDone_unmarkedTask_marksTaskAsDone() {
        Task task = new Task("wash dishes");
        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[X]", task.getMark());
    }

    @Test
    public void markNotDone_markedTask_marksTaskAsNotDone() {
        Task task = new Task("wash dishes");
        task.markDone();
        task.markNotDone();
        assertFalse(task.isDone());
        assertEquals("[ ]", task.getMark());
    }

    // ==========================================
    // Serialization (toFileString & toString) Tests
    // ==========================================

    @Test
    public void toFileString_unmarkedTask_returnsUnmarkedFormat() {
        Task task = new Task("wash dishes");
        assertEquals("0 | wash dishes", task.toFileString());
    }

    @Test
    public void toFileString_markedTask_returnsMarkedFormat() {
        Task task = new Task("wash dishes");
        task.markDone();
        assertEquals("1 | wash dishes", task.toFileString());
    }

    @Test
    public void toString_unmarkedTask_returnsFormattedString() {
        Task task = new Task("wash dishes");
        assertEquals("[ ] wash dishes", task.toString());
    }

    @Test
    public void toString_markedTask_returnsFormattedString() {
        Task task = new Task("wash dishes");
        task.markDone();
        assertEquals("[X] wash dishes", task.toString());
    }

    // ==========================================
    // Deserialization (fromFileString) Tests
    // ==========================================

    @Test
    public void fromFileString_validTodoUnmarked_returnsUnmarkedToDo() throws UserInputException {
        Task task = Task.fromFileString("T | 0 | read book");
        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void fromFileString_validTodoMarked_returnsMarkedToDo() throws UserInputException {
        Task task = Task.fromFileString("T | 1 | read book");
        assertInstanceOf(ToDo.class, task);
        assertEquals("read book", task.getDescription());
        assertTrue(task.isDone());
    }

    @Test
    public void fromFileString_validDeadlineUnmarked_returnsUnmarkedDeadline() throws UserInputException {
        Task task = Task.fromFileString("D | 0 | return book | 2026-06-06");
        assertInstanceOf(Deadline.class, task);
        Deadline deadline = (Deadline) task;
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.parse("2026-06-06"), deadline.getBy());
        assertFalse(deadline.isDone());
    }

    @Test
    public void fromFileString_validDeadlineMarked_returnsMarkedDeadline() throws UserInputException {
        Task task = Task.fromFileString("D | 1 | return book | 2026-06-06");
        assertInstanceOf(Deadline.class, task);
        Deadline deadline = (Deadline) task;
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.parse("2026-06-06"), deadline.getBy());
        assertTrue(deadline.isDone());
    }

    @Test
    public void fromFileString_validEventUnmarked_returnsUnmarkedEvent() throws UserInputException {
        Task task = Task.fromFileString("E | 0 | project meeting | 2026-08-06 | 2026-08-08");
        assertInstanceOf(Event.class, task);
        Event event = (Event) task;
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.parse("2026-08-06"), event.getFrom());
        assertEquals(LocalDate.parse("2026-08-08"), event.getTo());
        assertFalse(event.isDone());
    }

    @Test
    public void fromFileString_validEventMarked_returnsMarkedEvent() throws UserInputException {
        Task task = Task.fromFileString("E | 1 | project meeting | 2026-08-06 | 2026-08-08");
        assertInstanceOf(Event.class, task);
        Event event = (Event) task;
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.parse("2026-08-06"), event.getFrom());
        assertEquals(LocalDate.parse("2026-08-08"), event.getTo());
        assertTrue(event.isDone());
    }

    @Test
    public void fromFileString_nullString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString(null));
    }

    @Test
    public void fromFileString_emptyString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("   "));
    }

    @Test
    public void fromFileString_insufficientParts_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("T | 0"));
    }

    @Test
    public void fromFileString_invalidDoneStatus_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("T | 2 | read book"));
    }

    @Test
    public void fromFileString_emptyTodoDescription_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("T | 0 |   "));
    }

    @Test
    public void fromFileString_corruptedDeadlineMissingFields_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("D | 0 | return book"));
    }

    @Test
    public void fromFileString_corruptedDeadlineInvalidDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("D | 0 | return book | invalid-date"));
    }

    @Test
    public void fromFileString_corruptedEventMissingFields_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("E | 0 | meeting | 2026-08-06"));
    }

    @Test
    public void fromFileString_corruptedEventInvalidDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("E | 0 | meeting | 2026-08-06 | invalid-date"));
    }

    @Test
    public void fromFileString_unknownTaskType_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Task.fromFileString("X | 0 | unknown"));
    }
}
