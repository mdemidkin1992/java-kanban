package managers;

import managers.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskHistory = new ArrayList<>();
    private final int viewsCountLimit = 10;

    @Override
    public void add(Task task) {
        if (taskHistory.size() < viewsCountLimit) {
            taskHistory.add(task);
        } else {
            taskHistory.remove(0);
            taskHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
