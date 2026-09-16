package james.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Verifies the response contracts shared by both interfaces.
 */
public class CommandResponseTest {
    @Test
    public void constructor_textResponse_preservesFieldsWithoutSticker() {
        CommandResponse response = new CommandResponse("bye", CommandResponse.Type.NORMAL, true);
        assertEquals("bye", response.getMessage());
        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertTrue(response.isExit());
        assertNull(response.getSticker());
        assertNull(response.getStickerPath());
        assertEquals(CommandResponse.DisplayMode.TEXT_ONLY, response.getDisplayMode());
    }

    @Test
    public void createWithSticker_validFields_preservesTextAndCategory() {
        CommandResponse response = CommandResponse.createWithSticker("error", CommandResponse.Type.ERROR,
                Sticker.GOMEN);
        assertEquals("error", response.getMessage());
        assertEquals(CommandResponse.Type.ERROR, response.getType());
        assertEquals(Sticker.GOMEN, response.getSticker());
        assertEquals(Sticker.GOMEN.getResourcePath(), response.getStickerPath());
        assertEquals(CommandResponse.DisplayMode.STICKER_WITH_TEXT, response.getDisplayMode());
        assertFalse(response.isExit());
    }

    @Test
    public void createStickerOnly_validFields_preservesConsoleFallback() {
        CommandResponse response = CommandResponse.createStickerOnly("fallback", Sticker.YATTA);
        assertEquals("fallback", response.getMessage());
        assertEquals(CommandResponse.Type.NORMAL, response.getType());
        assertEquals(Sticker.YATTA, response.getSticker());
        assertEquals(Sticker.YATTA.getResourcePath(), response.getStickerPath());
        assertEquals(CommandResponse.DisplayMode.STICKER_ONLY, response.getDisplayMode());
        assertFalse(response.isExit());
    }

    @Test
    public void createWithSticker_nullSticker_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> CommandResponse.createWithSticker("text", CommandResponse.Type.NORMAL, null));
    }

    @Test
    public void createStickerOnly_nullSticker_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CommandResponse.createStickerOnly("text", null));
    }
}
