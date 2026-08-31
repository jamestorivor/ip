package james.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import james.command.Command;
import james.exception.UserInputException;
import james.task.Deadline;
import james.task.Event;
import james.task.Task;
import james.task.ToDo;

/**
 * Unit tests for {@link Parser}.
 */
public class ParserTest {

    // ==========================================
    // parseCommand Tests
    // ==========================================

    @Test
    public void parseCommand_singleWord_returnsSingleElementArray() throws UserInputException {
        String[] result = Parser.parseCommand("list");
        assertArrayEquals(new String[]{"list"}, result);
    }

    @Test
    public void parseCommand_commandWithArguments_returnsSplitArray() throws UserInputException {
        String[] result = Parser.parseCommand("todo read book");
        assertArrayEquals(new String[]{"todo", "read book"}, result);
    }

    @Test
    public void parseCommand_commandWithWhitespace_trimsCorrectly() throws UserInputException {
        String[] result = Parser.parseCommand("   mark 2   ");
        assertArrayEquals(new String[]{"mark", "2"}, result);
    }

    @Test
    public void parseCommand_nullString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommand(null));
    }

    @Test
    public void parseCommand_emptyString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommand(""));
    }

    @Test
    public void parseCommand_whitespaceOnly_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommand("   "));
    }

    // ==========================================
    // parseCommandType Tests
    // ==========================================

    @Test
    public void parseCommandType_validLowerCase_returnsCommand() throws UserInputException {
        assertEquals(Command.TODO, Parser.parseCommandType("todo"));
        assertEquals(Command.DEADLINE, Parser.parseCommandType("deadline"));
        assertEquals(Command.EVENT, Parser.parseCommandType("event"));
        assertEquals(Command.MARK, Parser.parseCommandType("mark"));
        assertEquals(Command.UNMARK, Parser.parseCommandType("unmark"));
        assertEquals(Command.DELETE, Parser.parseCommandType("delete"));
        assertEquals(Command.LIST, Parser.parseCommandType("list"));
        assertEquals(Command.BYE, Parser.parseCommandType("bye"));
        assertEquals(Command.LIST_BY_DATE, Parser.parseCommandType("list_by_date"));
    }

    @Test
    public void parseCommandType_validUpperCase_returnsCommand() throws UserInputException {
        assertEquals(Command.TODO, Parser.parseCommandType("TODO"));
    }

    @Test
    public void parseCommandType_invalidCommand_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommandType("unknown"));
    }

    // ==========================================
    // parseTaskNumber Tests
    // ==========================================

    @Test
    public void parseTaskNumber_validNumber_returnsZeroBasedIndex() throws UserInputException {
        assertEquals(0, Parser.parseTaskNumber("1", "mark", 3));
        assertEquals(2, Parser.parseTaskNumber("3", "delete", 3));
    }

    @Test
    public void parseTaskNumber_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber(null, "mark", 3));
    }

    @Test
    public void parseTaskNumber_emptyArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("   ", "mark", 3));
    }

    @Test
    public void parseTaskNumber_nonNumeric_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("abc", "mark", 3));
    }

    @Test
    public void parseTaskNumber_zeroNumber_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("0", "mark", 3));
    }

    @Test
    public void parseTaskNumber_negativeNumber_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("-1", "mark", 3));
    }

    @Test
    public void parseTaskNumber_exceedsListSize_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("4", "delete", 3));
    }

    // ==========================================
    // parseDate Tests
    // ==========================================

    @Test
    public void parseDate_validDateString_returnsParsedDate() throws UserInputException {
        LocalDate expected = LocalDate.of(2026, 5, 20);
        assertEquals(expected, Parser.parseDate("2026-05-20"));
    }

    @Test
    public void parseDate_nullString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate(null));
    }

    @Test
    public void parseDate_emptyString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate("   "));
    }

    @Test
    public void parseDate_invalidFormat_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate("20-05-2026"));
    }

    @Test
    public void parseDate_nonExistentDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate("2026-02-30"));
    }

    // ==========================================
    // parseTodo Tests
    // ==========================================

    @Test
    public void parseTodo_validDescription_returnsToDoTask() throws UserInputException {
        Task task = Parser.parseTodo("read novel");
        assertInstanceOf(ToDo.class, task);
        assertEquals("read novel", task.getDescription());
    }

    @Test
    public void parseTodo_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTodo(null));
    }

    @Test
    public void parseTodo_emptyArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTodo("   "));
    }

    // ==========================================
    // parseDeadline Tests
    // ==========================================

    @Test
    public void parseDeadline_validArguments_returnsDeadlineTask() throws UserInputException {
        Task task = Parser.parseDeadline("submit assignment /by 2026-10-15");
        assertInstanceOf(Deadline.class, task);
        Deadline deadline = (Deadline) task;
        assertEquals("submit assignment", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 10, 15), deadline.getBy());
    }

    @Test
    public void parseDeadline_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline(null));
    }

    @Test
    public void parseDeadline_missingByKeyword_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("submit assignment 2026-10-15"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("/by 2026-10-15"));
    }

    @Test
    public void parseDeadline_emptyDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("submit assignment /by "));
    }

    @Test
    public void parseDeadline_invalidDateFormat_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("submit assignment /by tomorrow"));
    }

    // ==========================================
    // parseEvent Tests
    // ==========================================

    @Test
    public void parseEvent_validArguments_returnsEventTask() throws UserInputException {
        Task task = Parser.parseEvent("hackathon /from 2026-11-01 /to 2026-11-03");
        assertInstanceOf(Event.class, task);
        Event event = (Event) task;
        assertEquals("hackathon", event.getDescription());
        assertEquals(LocalDate.of(2026, 11, 1), event.getFrom());
        assertEquals(LocalDate.of(2026, 11, 3), event.getTo());
    }

    @Test
    public void parseEvent_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent(null));
    }

    @Test
    public void parseEvent_missingFromKeyword_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /to 2026-11-03"));
    }

    @Test
    public void parseEvent_missingToKeyword_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /from 2026-11-01"));
    }

    @Test
    public void parseEvent_emptyDescription_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("/from 2026-11-01 /to 2026-11-03"));
    }

    @Test
    public void parseEvent_emptyDates_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /from /to "));
    }

    @Test
    public void parseEvent_invalidDates_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /from today /to tomorrow"));
    }
}
