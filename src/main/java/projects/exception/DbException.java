package projects.exception;

@SuppressWarnings("serial")
public class DbException extends RuntimeException {

    public DbException(String message) {
        super(message); //super is the base class that a subclass is deriving from
    }

    public DbException(Throwable cause) {

        super(cause);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause);

    }
}
