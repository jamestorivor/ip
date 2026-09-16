package james.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import james.command.Command;
import james.exception.UserInputException;
import james.task.Task;

/**
 * Verifies description safety and locale-independent command recognition.
 */
public class ParserValidationTest {
    @Test
    public void parseTodo_reservedDescriptions_reportsSpecificError() {
        for (String description : invalidDescriptions()) {
            assertDescriptionError(assertThrows(UserInputException.class, () -> Parser.parseTodo(description)));
        }
    }

    @Test
    public void parseDeadline_reservedDescriptions_reportsSpecificError() {
        for (String description : invalidDescriptions()) {
            assertDescriptionError(assertThrows(UserInputException.class,
                    () -> Parser.parseDeadline(description + " /by 2026-09-16")));
        }
    }

    @Test
    public void parseEvent_reservedDescriptions_reportsSpecificError() {
        for (String description : invalidDescriptions()) {
            assertDescriptionError(assertThrows(UserInputException.class,
                    () -> Parser.parseEvent(description + " /from 2026-09-16 /to 2026-09-18")));
        }
    }

    @Test
    public void parseTasks_unicodeDescriptions_preservesText() throws UserInputException {
        String description = "读书 📚";
        Task[] tasks = {Parser.parseTodo(description), Parser.parseDeadline(description + " /by 2026-09-16"),
            Parser.parseEvent(description + " /from 2026-09-16 /to 2026-09-18")};
        for (Task task : tasks) {
            assertEquals(description, task.getDescription());
        }
    }

    @Test
    @ResourceLock("defaultLocale")
    public void parseCommandType_turkishLocale_recognizesEnglishCommands() throws UserInputException {
        Locale original = Locale.getDefault();
        Locale display = Locale.getDefault(Locale.Category.DISPLAY);
        Locale format = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Locale.setDefault(Locale.Category.FORMAT, format);
            for (Command command : Command.values()) {
                assertEquals(command, Parser.parseCommandType(command.name().toLowerCase(Locale.ROOT)));
            }
        } finally {
            Locale.setDefault(original);
            Locale.setDefault(Locale.Category.DISPLAY, display);
            Locale.setDefault(Locale.Category.FORMAT, format);
        }
    }

    /**
     * Returns embedded forbidden characters without depending on surrounding whitespace trimming.
     */
    private String[] invalidDescriptions() {
        return new String[]{"bad | record", "bad\trecord", "bad\nrecord", "bad\rrecord",
            "bad" + (char) 0 + "record", "bad" + (char) 27 + "record"};
    }

    /**
     * Checks the description validation error rather than an unrelated parsing failure.
     */
    private void assertDescriptionError(UserInputException exception) {
        assertEquals("Task descriptions cannot contain | or control characters.", exception.getMessage());
        assertEquals(UserInputException.Category.INPUT, exception.getCategory());
    }
}
