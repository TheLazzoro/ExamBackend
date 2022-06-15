package errorhandling;

public class UserAlreadyExistsException extends Exception {
    private int errorCode;

    public UserAlreadyExistsException(String message) {
        super (message);
    }

    public int getErrorCode() {
        return errorCode;
    }
}
