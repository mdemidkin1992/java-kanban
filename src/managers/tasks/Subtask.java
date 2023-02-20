package managers.tasks;

import managers.enums.TaskStatus;

import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
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
                + durationMinutes + " минут,"
                + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm"));
    }
}
