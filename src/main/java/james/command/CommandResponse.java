package james.command;

/**
 * Represents the result of processing one user command.
 */
public class CommandResponse {
    /**
     * Describes the response category used by graphical styling.
     */
    public enum Type { NORMAL, ADD, MARK, DELETE, ERROR }

    private final String message;
    private final Type type;
    private final boolean isExit;
    private final String stickerPath;

    /**
     * Creates a command response.
     *
     * @param message Response text.
     * @param type Response category.
     * @param isExit Whether the application should exit.
     */
    public CommandResponse(String message, Type type, boolean isExit) {
        this(message, type, isExit, null);
    }

    /**
     * Creates a response with an optional sticker resource.
     *
     * @param message Response text, also used by the console.
     * @param type Response category.
     * @param isExit Whether the application should exit.
     * @param stickerPath Classpath image resource, or null for a text response.
     */
    public CommandResponse(String message, Type type, boolean isExit, String stickerPath) {
        this.stickerPath = stickerPath;
        this.message = message;
        this.type = type;
        this.isExit = isExit;
    }

    public String getStickerPath() { return stickerPath; }
    public String getMessage() { return message; }
    public Type getType() { return type; }
    public boolean isExit() { return isExit; }
}
