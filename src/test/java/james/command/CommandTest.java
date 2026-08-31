package james.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Command}.
 */
public class CommandTest {

    @Test
    public void valueOf_validCommandNames_returnsCorrectEnumConstant() {
        assertEquals(Command.DELETE, Command.valueOf("DELETE"));
        assertEquals(Command.TODO, Command.valueOf("TODO"));
        assertEquals(Command.EVENT, Command.valueOf("EVENT"));
        assertEquals(Command.DEADLINE, Command.valueOf("DEADLINE"));
        assertEquals(Command.MARK, Command.valueOf("MARK"));
        assertEquals(Command.UNMARK, Command.valueOf("UNMARK"));
        assertEquals(Command.LIST, Command.valueOf("LIST"));
        assertEquals(Command.BYE, Command.valueOf("BYE"));
        assertEquals(Command.LIST_BY_DATE, Command.valueOf("LIST_BY_DATE"));
    }

    @Test
    public void valueOf_invalidCommandName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Command.valueOf("INVALID_COMMAND"));
    }

    @Test
    public void values_allDefinedCommands_containsNineCommands() {
        Command[] commands = Command.values();
        assertNotNull(commands);
        assertEquals(9, commands.length);
    }
}
