package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TaskList}.
 */
public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    // ==========================================
    // Constructor & Initialization Tests
    // ==========================================

    @Test
    public void constructor_default_createsEmptyList() {
        TaskList emptyList = new TaskList();
        assertEquals(0, emptyList.size());
        assertEquals(new ArrayList<>(), emptyList.getTasks());
    }

    @Test
    public void constructor_nullList_createsEmptyList() {
        TaskList nullList = new TaskList(null);
        assertEquals(0, nullList.size());
        assertEquals(new ArrayList<>(), nullList.getTasks());
    }

    @Test
    public void constructor_existingList_initializesWithTasks() {
        ArrayList<Task> initialList = new ArrayList<>();
        ToDo task1 = new ToDo("Task 1");
        Deadline task2 = new Deadline("Task 2", LocalDate.parse("2026-10-10"));
        initialList.add(task1);
        initialList.add(task2);

        TaskList initializedList = new TaskList(initialList);
        assertEquals(2, initializedList.size());
        assertEquals(task1, initializedList.getTask(0));
        assertEquals(task2, initializedList.getTask(1));
    }

    // ==========================================
    // Add & Get & Size Tests
    // ==========================================

    @Test
    public void size_emptyList_returnsZero() {
        assertEquals(0, taskList.size());
    }

    @Test
    public void addTask_validTask_taskAddedAndSizeIncremented() {
        ToDo task = new ToDo("Buy groceries");
        taskList.addTask(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.getTask(0));
    }

    @Test
    public void getTask_validIndex_returnsCorrectTask() {
        ToDo task1 = new ToDo("Task 1");
        Deadline task2 = new Deadline("Task 2", LocalDate.parse("2026-12-01"));
        taskList.addTask(task1);
        taskList.addTask(task2);

        assertEquals(task1, taskList.getTask(0));
        assertEquals(task2, taskList.getTask(1));
    }

    @Test
    public void getTask_negativeIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(-1));
    }

    @Test
    public void getTask_outOfBoundsIndex_throwsIndexOutOfBoundsException() {
        taskList.addTask(new ToDo("Task 1"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(1));
    }

    @Test
    public void getTasks_returnsUnderlyingList() {
        ToDo task = new ToDo("Test task");
        taskList.addTask(task);

        ArrayList<Task> underlyingList = taskList.getTasks();
        assertEquals(1, underlyingList.size());
        assertEquals(task, underlyingList.get(0));
    }

    // ==========================================
    // Delete Task Tests
    // ==========================================

    @Test
    public void deleteTask_validIndex_removesAndReturnsTask() {
        ToDo task1 = new ToDo("Task 1");
        ToDo task2 = new ToDo("Task 2");
        taskList.addTask(task1);
        taskList.addTask(task2);

        Task removedTask = taskList.deleteTask(0);
        assertEquals(task1, removedTask);
        assertEquals(1, taskList.size());
        assertEquals(task2, taskList.getTask(0));
    }

    @Test
    public void deleteTask_outOfBoundsIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.deleteTask(1));
    }

    @Test
    public void deleteTask_negativeIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.deleteTask(-1));
    }

    @Test
    public void deleteTask_byTaskObject_removesTaskFromList() {
        ToDo task1 = new ToDo("Task 1");
        ToDo task2 = new ToDo("Task 2");
        taskList.addTask(task1);
        taskList.addTask(task2);

        taskList.deleteTask(task1);
        assertEquals(1, taskList.size());
        assertEquals(task2, taskList.getTask(0));
    }

    @Test
    public void deleteTask_nonExistentTaskObject_listRemainsUnchanged() {
        ToDo task1 = new ToDo("Task 1");
        ToDo nonExistentTask = new ToDo("Non-existent Task");
        taskList.addTask(task1);

        taskList.deleteTask(nonExistentTask);
        assertEquals(1, taskList.size());
        assertEquals(task1, taskList.getTask(0));
    }

    // ==========================================
    // taskDateMatch (Static) Tests
    // ==========================================

    @Test
    public void taskDateMatch_deadlineMatchingDate_returnsTrue() {
        LocalDate deadlineDate = LocalDate.parse("2026-05-15");
        Deadline deadline = new Deadline("Submit assignment", deadlineDate);
        assertTrue(TaskList.taskDateMatch(deadline, deadlineDate));
    }

    @Test
    public void taskDateMatch_deadlineDifferentDate_returnsFalse() {
        LocalDate deadlineDate = LocalDate.parse("2026-05-15");
        LocalDate differentDate = LocalDate.parse("2026-05-16");
        Deadline deadline = new Deadline("Submit assignment", deadlineDate);
        assertFalse(TaskList.taskDateMatch(deadline, differentDate));
    }

    @Test
    public void taskDateMatch_eventOnStartDate_returnsTrue() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        Event event = new Event("Camp", startDate, endDate);
        assertTrue(TaskList.taskDateMatch(event, startDate));
    }

    @Test
    public void taskDateMatch_eventOnEndDate_returnsTrue() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        Event event = new Event("Camp", startDate, endDate);
        assertTrue(TaskList.taskDateMatch(event, endDate));
    }

    @Test
    public void taskDateMatch_eventWithinDateRange_returnsTrue() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate intermediateDate = LocalDate.parse("2026-06-03");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        Event event = new Event("Camp", startDate, endDate);
        assertTrue(TaskList.taskDateMatch(event, intermediateDate));
    }

    @Test
    public void taskDateMatch_eventBeforeStartDate_returnsFalse() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        LocalDate dateBefore = LocalDate.parse("2026-05-31");
        Event event = new Event("Camp", startDate, endDate);
        assertFalse(TaskList.taskDateMatch(event, dateBefore));
    }

    @Test
    public void taskDateMatch_eventAfterEndDate_returnsFalse() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        LocalDate dateAfter = LocalDate.parse("2026-06-06");
        Event event = new Event("Camp", startDate, endDate);
        assertFalse(TaskList.taskDateMatch(event, dateAfter));
    }

    @Test
    public void taskDateMatch_toDoTask_returnsFalse() {
        ToDo todo = new ToDo("Read book");
        LocalDate date = LocalDate.parse("2026-06-01");
        assertFalse(TaskList.taskDateMatch(todo, date));
    }

    @Test
    public void taskDateMatch_genericTask_returnsFalse() {
        Task genericTask = new Task("Generic task");
        LocalDate date = LocalDate.parse("2026-06-01");
        assertFalse(TaskList.taskDateMatch(genericTask, date));
    }

    // ==========================================
    // getTasksOnDate Tests
    // ==========================================

    @Test
    public void getTasksOnDate_toDoTask_returnsEmptyList() {
        taskList.addTask(new ToDo("Read book"));
        LocalDate date = LocalDate.parse("2026-11-11");
        ArrayList<Task> results = taskList.getTasksOnDate(date);
        assertEquals(0, results.size());
        assertEquals(new ArrayList<>(), results);
    }

    @Test
    public void getTasksOnDate_dateWithinEventRange_returnsEvent() {
        LocalDate targetDate = LocalDate.parse("2026-11-12");
        Event matchingEvent = new Event("Conference",
                LocalDate.parse("2026-11-11"),
                LocalDate.parse("2026-11-15"));
        taskList.addTask(matchingEvent);
        ArrayList<Task> results = taskList.getTasksOnDate(targetDate);
        assertEquals(1, results.size());
        assertEquals(matchingEvent, results.get(0));
    }

    @Test
    public void getTasksOnDate_dateOutsideEventRange_returnsEmptyList() {
        LocalDate targetDate = LocalDate.parse("2026-11-12");
        Event futureEvent = new Event("Workshop",
                LocalDate.parse("2026-11-13"),
                LocalDate.parse("2026-11-15"));
        taskList.addTask(futureEvent);
        ArrayList<Task> results = taskList.getTasksOnDate(targetDate);
        assertEquals(0, results.size());
    }

    @Test
    public void getTasksOnDate_mixedTasksWithMatches_returnsMatchingTasksInOrder() {
        LocalDate targetDate = LocalDate.parse("2026-10-15");

        ToDo todo = new ToDo("Read book");
        Deadline matchingDeadline = new Deadline("Submit report", LocalDate.parse("2026-10-15"));
        Deadline nonMatchingDeadline = new Deadline("Pay bills", LocalDate.parse("2026-10-20"));
        Event matchingEvent = new Event("Career Fair",
                LocalDate.parse("2026-10-14"),
                LocalDate.parse("2026-10-16"));
        Event nonMatchingEvent = new Event("Hackathon",
                LocalDate.parse("2026-10-17"),
                LocalDate.parse("2026-10-18"));

        taskList.addTask(todo);
        taskList.addTask(matchingDeadline);
        taskList.addTask(nonMatchingDeadline);
        taskList.addTask(matchingEvent);
        taskList.addTask(nonMatchingEvent);

        ArrayList<Task> results = taskList.getTasksOnDate(targetDate);
        assertEquals(2, results.size());
        assertEquals(matchingDeadline, results.get(0));
        assertEquals(matchingEvent, results.get(1));
    }

    // ==========================================
    // toString Tests
    // ==========================================

    @Test
    public void toString_emptyList_returnsEmptyString() {
        assertEquals("", taskList.toString());
    }

    @Test
    public void toString_multipleTasks_returnsNumberedString() {
        taskList.addTask(new ToDo("read book"));
        taskList.addTask(new Deadline("return book", LocalDate.parse("2026-06-06")));
        taskList.addTask(new Event("catch mouse", LocalDate.parse("2026-11-11"), LocalDate.parse("2026-11-11")));
        String expected = "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Jun 06 2026)\n"
                + "3.[E][ ] catch mouse (from: Nov 11 2026 to: Nov 11 2026)\n";
        assertEquals(expected, taskList.toString());
    }
}