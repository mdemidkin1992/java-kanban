package managers;

import managers.tasks.*;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int taskCounter = 1;

    public Task addTask(Task task) {
        task.setId(taskCounter++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setId(taskCounter++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(taskCounter++);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        syncEpicStatus(subtask.getEpicId());
        return subtask;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public Task updateTask(Integer taskId, Task updatedTask) {
        if (tasks.containsKey(taskId)) {
            updatedTask.setId(taskId);
            tasks.put(updatedTask.getId(), updatedTask);
        }
        return tasks.get(taskId);
    }

    public Subtask updateSubtask(Integer subtaskId, Subtask updatedSubtask) {
        if (subtasks.containsKey(subtaskId)) {
            updatedSubtask.setId(subtaskId);
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            epics.get(subtasks.get(subtaskId).getEpicId()).removeSubtaskId(subtaskId);
            epics.get(subtasks.get(subtaskId).getEpicId()).addSubtaskId(subtaskId);
            syncEpicStatus(subtasks.get(subtaskId).getEpicId());
        }
        return subtasks.get(subtaskId);
    }

    public Epic updateEpic(Integer epicId, Epic updatedEpic) {
        if (epics.containsKey(epicId)) {
            updatedEpic.setId(epicId);
            epics.put(updatedEpic.getId(), updatedEpic);
            syncEpicStatus(epicId);
        }
        return epics.get(epicId);
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Integer epicId) {
        ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer id : subtaskIds) {
            if (subtasks.containsKey(id)) {
                epicSubtasks.add(subtasks.get(id));
            }
        }
        return epicSubtasks;
    }

    public void deleteAnyTask(Integer anyTaskId) {
        if (tasks.containsKey(anyTaskId)) {
            tasks.remove(anyTaskId);
        } else if (subtasks.containsKey(anyTaskId)) {
            epics.get(subtasks.get(anyTaskId).getEpicId()).removeSubtaskId(anyTaskId);
            syncEpicStatus(subtasks.get(anyTaskId).getEpicId());
            subtasks.remove(anyTaskId);
        } else if (epics.containsKey(anyTaskId)) {
            epics.remove(anyTaskId);
        } else {
            System.out.println("Такой задачи пока нет");
        }
    }

    public Object getAnyTask(Integer anyTaskId) {
        Object result = new Object();
        if (tasks.containsKey(anyTaskId)) {
            result = tasks.get(anyTaskId);
        } else if (subtasks.containsKey(anyTaskId)) {
            result = subtasks.get(anyTaskId);
        } else if (epics.containsKey(anyTaskId)) {
            result = epics.get(anyTaskId);
        } else {
            System.out.println("Такой задачи пока нет");
        }
        return result;
    }

    private void syncEpicStatus(Integer epicId) {
        int countNew = 0;
        int countDone = 0;
        ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();

        if (epics.get(epicId).getSubtaskIds() == null) {
            epics.get(epicId).setStatus("NEW");
        } else {
            for (Integer subtaskId : subtaskIds) {
                if (subtasks.get(subtaskId).getStatus() == "NEW") {
                    countNew++;
                } else if (subtasks.get(subtaskId).getStatus() == "DONE") {
                    countDone++;
                }
            }

            if (countNew == subtaskIds.size()) {
                epics.get(epicId).setStatus("NEW");
            } else if (countDone == subtaskIds.size()) {
                epics.get(epicId).setStatus("DONE");
            } else {
                epics.get(epicId).setStatus("IN_PROGRESS");
            }
        }
    }
}