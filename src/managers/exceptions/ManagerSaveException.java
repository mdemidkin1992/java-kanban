package managers.exceptions;

import java.io.IOException;

public class ManagerSaveException extends IOException {
    public ManagerSaveException(String description) {
        super(description);
    }
}
