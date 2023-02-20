package managers.taskmanagers;

import managers.tasks.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Map<Integer, Task> getAllTasks();

    Map<Integer, Subtask> getAllSubtasks();

    Map<Integer, Epic> getAllEpics();

    void deleteAllTasks();

    Task updateTask(int taskId, Task updatedTask);

    Subtask updateSubtask(int subtaskId, Subtask updatedSubtask);

    Epic updateEpic(int epicId, Epic updatedEpic);

    List<Subtask> getSubtasksFromEpic(int epicId);

    void deleteAnyTask(int anyTaskId);

    Task getAnyTask(int anyTaskId);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

}