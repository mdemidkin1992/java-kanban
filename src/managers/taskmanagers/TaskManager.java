package managers.taskmanagers;

import managers.exceptions.ManagerSaveException;
import managers.tasks.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TaskManager {

    Task addTask(Task task) throws IOException;

    Epic addEpic(Epic epic) throws IOException;

    Subtask addSubtask(Subtask subtask) throws IOException;

    Map<Integer, Task> getAllTasks() throws ManagerSaveException;

    Map<Integer, Subtask> getAllSubtasks() throws ManagerSaveException;

    Map<Integer, Epic> getAllEpics() throws ManagerSaveException;

    void deleteAllTasks() throws ManagerSaveException;

    Task updateTask(Integer taskId, Task updatedTask) throws ManagerSaveException;

    Subtask updateSubtask(Integer subtaskId, Subtask updatedSubtask) throws ManagerSaveException;

    Epic updateEpic(Integer epicId, Epic updatedEpic) throws ManagerSaveException;

    List<Subtask> getSubtasksFromEpic(Integer epicId) throws ManagerSaveException;

    void deleteAnyTask(Integer anyTaskId) throws ManagerSaveException;

    Object getAnyTask(Integer anyTaskId) throws IOException;

    List<Task> getHistory() throws IOException;

}