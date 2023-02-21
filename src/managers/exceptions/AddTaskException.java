package managers.exceptions;

/**
 * @author Maxim Demidkin
 * @Date 21.02.2023
 */

public class AddTaskException extends Exception {
    public AddTaskException(String description) {
        super(description);
    }
}
