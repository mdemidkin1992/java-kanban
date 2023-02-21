package managers.tasks;

import managers.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status, int durationMinutes, LocalDateTime startTime) {
        super(name, description, status, durationMinutes, startTime);
    }

    public void addSubtaskId(Integer subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setDurationMinutes(List<Integer> subtasksDurationMinutes) {
        int epicDurationMinutes = 0;
        if (subtasksDurationMinutes != null) {
            for (int subtaskDurationMinutes : subtasksDurationMinutes) {
                epicDurationMinutes += subtaskDurationMinutes;
            }
        }
        this.durationMinutes = epicDurationMinutes;
    }

    public void setStartTime(List<LocalDateTime> subtasksStartTime) {
        this.startTime = subtasksStartTime.stream()
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void setEndTime(List<LocalDateTime> subtasksEndTime) {
        this.endTime = subtasksEndTime.stream()
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public String toString() {
        return "\n" + id + ","
                + taskType + ","
                + name + ","
                + status + ","
                + description + ","
                + durationMinutes + ","
                + startTime;
    }

}