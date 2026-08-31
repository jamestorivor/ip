package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void constructor_validParameters_setsDescriptionAndByDate() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals("submit report", deadline.getDescription());
        assertEquals(date, deadline.getBy());
        assertFalse(deadline.isDone());
    }

    @Test
    public void toFileString_unmarkedDeadline_returnsFormattedString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals("D | 0 | submit report | 2026-06-06", deadline.toFileString());
    }

    @Test
    public void toFileString_markedDeadline_returnsFormattedString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);
        deadline.markDone();

        assertTrue(deadline.isDone());
        assertEquals("D | 1 | submit report | 2026-06-06", deadline.toFileString());
    }

    @Test
    public void toString_unmarkedDeadline_returnsDisplayString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals("[D][ ] submit report (by: Jun 06 2026)", deadline.toString());
    }

    @Test
    public void toString_markedDeadline_returnsDisplayString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);
        deadline.markDone();

        assertEquals("[D][X] submit report (by: Jun 06 2026)", deadline.toString());
    }
}
