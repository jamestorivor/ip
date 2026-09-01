package james.exception;

/**
 * Signals an error caused by invalid or malformed user input or data.
 */
public class UserInputException extends Exception {

    /**
     * Constructs a UserInputException with the specified error message.
     *
     * @param message Description of the user error.
     */
    public UserInputException(String message) {
        super(message);
    }
}
