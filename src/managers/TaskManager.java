package managers;

import managers.tasks.*;

import java.util.List;
import java.util.Map;

public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Map<Integer, Task> getAllTasks();

    Map<Integer, Subtask> getAllSubtasks();

    Map<Integer, Epic> getAllEpics();

    void deleteAllTasks();

    Task updateTask(Integer taskId, Task updatedTask);

    Subtask updateSubtask(Integer subtaskId, Subtask updatedSubtask);

    Epic updateEpic(Integer epicId, Epic updatedEpic);

    List<Subtask> getSubtasksFromEpic(Integer epicId);

    void deleteAnyTask(Integer anyTaskId);

    Object getAnyTask(Integer anyTaskId);

    List<Task> getHistory();

}