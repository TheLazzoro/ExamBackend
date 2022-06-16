package errorhandling;

/**
 *
 * @author lam@cphbusiness.dk
 */
public class NotFoundException extends Exception {

    private int errorCode = 404;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
        super("Requested item could not be found");
    }

    public int getErrorCode() {
        return errorCode;
    }
}
