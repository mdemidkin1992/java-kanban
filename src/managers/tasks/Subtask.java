package managers.tasks;

import managers.enums.TaskStatus;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId, int durationMinutes, LocalDateTime startTime) {
        super(name, description, status, durationMinutes, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "\n" + id + ","
                + taskType + ","
                + name + ","
                + status + ","
                + description + ","
                + epicId + ","
                + durationMinutes + ","
                + startTime;
    }
}
