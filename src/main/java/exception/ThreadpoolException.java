package exception;

public class ThreadpoolException extends RuntimeException {
    public ThreadpoolException(Throwable cause) {
        super(cause);
    }

    public ThreadpoolException(String message) {
        super(message);
    }
}
