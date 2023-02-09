package managers.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String description) {
        super(description);
    }
}
