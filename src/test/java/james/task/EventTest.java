package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Event}.
 */
public class EventTest {

    @Test
    public void constructor_validParameters_setsDescriptionAndDates() {
        LocalDate from = LocalDate.of(2026, 8, 6);
        LocalDate to = LocalDate.of(2026, 8, 8);
        Event event = new Event("orientation camp", from, to);

        assertEquals("orientation camp", event.getDescription());
        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
        assertFalse(event.isDone());
    }

    @Test
    public void toFileString_unmarkedEvent_returnsFormattedString() {
        LocalDate from = LocalDate.of(2026, 8, 6);
        LocalDate to = LocalDate.of(2026, 8, 8);
        Event event = new Event("orientation camp", from, to);

        assertEquals("E | 0 | orientation camp | 2026-08-06 | 2026-08-08", event.toFileString());
    }

    @Test
    public void toFileString_markedEvent_returnsFormattedString() {
        LocalDate from = LocalDate.of(2026, 8, 6);
        LocalDate to = LocalDate.of(2026, 8, 8);
        Event event = new Event("orientation camp", from, to);
        event.markDone();

        assertTrue(event.isDone());
        assertEquals("E | 1 | orientation camp | 2026-08-06 | 2026-08-08", event.toFileString());
    }

    @Test
    public void toString_unmarkedEvent_returnsDisplayString() {
        LocalDate from = LocalDate.of(2026, 8, 6);
        LocalDate to = LocalDate.of(2026, 8, 8);
        Event event = new Event("orientation camp", from, to);

        assertEquals("[E][ ] orientation camp (from: Aug 06 2026 to: Aug 08 2026)", event.toString());
    }

    @Test
    public void toString_markedEvent_returnsDisplayString() {
        LocalDate from = LocalDate.of(2026, 8, 6);
        LocalDate to = LocalDate.of(2026, 8, 8);
        Event event = new Event("orientation camp", from, to);
        event.markDone();

        assertEquals("[E][X] orientation camp (from: Aug 06 2026 to: Aug 08 2026)", event.toString());
    }
}
