package managers;

import managers.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        final int taskHistoryLimit = 10;
        taskHistory.add(task);
        if (taskHistory.size() > taskHistoryLimit) {
            taskHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
