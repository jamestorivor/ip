package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ToDo}.
 */
public class ToDoTest {

    @Test
    public void constructor_validDescription_setsDescription() {
        ToDo todo = new ToDo("buy groceries");
        assertEquals("buy groceries", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void toFileString_unmarkedTodo_returnsFormattedString() {
        ToDo todo = new ToDo("buy groceries");
        assertEquals("T | 0 | buy groceries", todo.toFileString());
    }

    @Test
    public void toFileString_markedTodo_returnsFormattedString() {
        ToDo todo = new ToDo("buy groceries");
        todo.markDone();

        assertTrue(todo.isDone());
        assertEquals("T | 1 | buy groceries", todo.toFileString());
    }

    @Test
    public void toString_unmarkedTodo_returnsDisplayString() {
        ToDo todo = new ToDo("buy groceries");
        assertEquals("[T][ ] buy groceries", todo.toString());
    }

    @Test
    public void toString_markedTodo_returnsDisplayString() {
        ToDo todo = new ToDo("buy groceries");
        todo.markDone();

        assertEquals("[T][X] buy groceries", todo.toString());
    }
}
