package managers.taskmanagers;

import managers.enums.TaskStatus;
import managers.enums.TaskType;
import managers.exceptions.ManagerSaveException;
import managers.historymanagers.HistoryManager;
import managers.tasks.*;
import managers.utilities.Managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected static final HistoryManager historyManager = Managers.getDefaultHistory();
    protected static final Map<Integer, Task> tasks = new HashMap<>();
    protected static final Map<Integer, Epic> epics = new HashMap<>();
    protected static final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int taskCounter = 1;

    @Override
    public Task addTask(Task task) throws IOException {
        task.setId(taskCounter++);
        task.setTaskType(TaskType.TASK);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) throws IOException {
        epic.setId(taskCounter++);
        epic.setTaskType(TaskType.EPIC);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) throws IOException {
        subtask.setId(taskCounter++);
        subtask.setTaskType(TaskType.SUBTASK);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        syncEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public Map<Integer, Task> getAllTasks() throws ManagerSaveException {
        return tasks;
    }

    @Override
    public Map<Integer, Subtask> getAllSubtasks() throws ManagerSaveException {
        return subtasks;
    }

    @Override
    public Map<Integer, Epic> getAllEpics() throws ManagerSaveException {
        return epics;
    }

    @Override
    public void deleteAllTasks() throws ManagerSaveException {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task updateTask(Integer taskId, Task updatedTask) throws ManagerSaveException {
        if (tasks.containsKey(taskId)) {
            updatedTask.setId(taskId);
            tasks.put(updatedTask.getId(), updatedTask);
        }
        return tasks.get(taskId);
    }

    @Override
    public Subtask updateSubtask(Integer subtaskId, Subtask updatedSubtask) throws ManagerSaveException {
        if (subtasks.containsKey(subtaskId)) {
            updatedSubtask.setId(subtaskId);
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            epics.get(subtasks.get(subtaskId).getEpicId()).removeSubtaskId(subtaskId);
            epics.get(subtasks.get(subtaskId).getEpicId()).addSubtaskId(subtaskId);
            syncEpicStatus(subtasks.get(subtaskId).getEpicId());
        }
        return subtasks.get(subtaskId);
    }

    @Override
    public Epic updateEpic(Integer epicId, Epic updatedEpic) throws ManagerSaveException {
        if (epics.containsKey(epicId)) {
            updatedEpic.setId(epicId);
            epics.put(updatedEpic.getId(), updatedEpic);
            syncEpicStatus(epicId);
        }
        return epics.get(epicId);
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(Integer epicId) throws ManagerSaveException {
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer id : subtaskIds) {
            if (subtasks.containsKey(id)) {
                epicSubtasks.add(subtasks.get(id));
            }
        }
        return epicSubtasks;
    }

    @Override
    public void deleteAnyTask(Integer anyTaskId) throws ManagerSaveException {
        historyManager.remove(anyTaskId);
        if (tasks.containsKey(anyTaskId)) {
            tasks.remove(anyTaskId);
        } else if (subtasks.containsKey(anyTaskId)) {
            epics.get(subtasks.get(anyTaskId).getEpicId()).removeSubtaskId(anyTaskId);
            syncEpicStatus(subtasks.get(anyTaskId).getEpicId());
            subtasks.remove(anyTaskId);
        } else if (epics.containsKey(anyTaskId)) {
            List<Integer> subtaskIds = epics.get(anyTaskId).getSubtaskIds();
            epics.remove(anyTaskId);
            if (subtaskIds != null) {
                for (Integer subtaskId : subtaskIds) {
                    historyManager.remove(subtaskId);
                }
            }
        } else {
            System.out.println("Такой задачи пока нет");
        }
    }

    @Override
    public Object getAnyTask(Integer anyTaskId) throws IOException {
        Object result = new Object();
        if (tasks.containsKey(anyTaskId)) {
            historyManager.add(tasks.get(anyTaskId));
            result = tasks.get(anyTaskId);
        } else if (subtasks.containsKey(anyTaskId)) {
            historyManager.add(subtasks.get(anyTaskId));
            result = subtasks.get(anyTaskId);
        } else if (epics.containsKey(anyTaskId)) {
            historyManager.add(epics.get(anyTaskId));
            result = epics.get(anyTaskId);
        } else {
            System.out.println("Такой задачи пока нет");
        }
        return result;
    }

    private void syncEpicStatus(Integer epicId) {
        int countNew = 0;
        int countDone = 0;
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();

        if (epics.get(epicId).getSubtaskIds() == null) {
            epics.get(epicId).setStatus(TaskStatus.NEW);
        } else {
            for (Integer subtaskId : subtaskIds) {
                switch (subtasks.get(subtaskId).getStatus()) {
                    case NEW:
                        countNew++;
                        break;
                    case IN_PROGRESS:
                        countDone++;
                        break;
                }
            }

            if (countNew == subtaskIds.size()) {
                epics.get(epicId).setStatus(TaskStatus.NEW);
            } else if (countDone == subtaskIds.size()) {
                epics.get(epicId).setStatus(TaskStatus.DONE);
            } else {
                epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}