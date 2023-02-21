package managers.taskmanagers;

import managers.enums.TaskStatus;
import managers.enums.TaskType;
import managers.exceptions.AddTaskException;
import managers.historymanagers.HistoryManager;
import managers.tasks.*;
import managers.utilities.Managers;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime
            , Comparator.nullsLast(Comparator.naturalOrder())));

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int taskCounter = 1;

    @Override
    public Task addTask(Task task) {
        task.setId(taskCounter++);
        task.setTaskType(TaskType.TASK);
        tasks.put(task.getId(), task);
        validateStartTime(task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(taskCounter++);
        epic.setTaskType(TaskType.EPIC);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask.setId(taskCounter++);
        subtask.setTaskType(TaskType.SUBTASK);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        syncEpic(subtask.getEpicId());
        validateStartTime(subtask);
        return subtask;
    }

    @Override
    public Map<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    @Override
    public Map<Integer, Epic> getAllEpics() {
        return epics;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task updateTask(int taskId, Task updatedTask) {
        if (tasks.containsKey(taskId)) {
            updatedTask.setId(taskId);
            updatedTask.setTaskType(TaskType.TASK);
            updatedTask.setStartTime(tasks.get(taskId).getStartTime());
            updatedTask.setDurationMinutes(tasks.get(taskId).getDurationMinutes());
            tasks.put(updatedTask.getId(), updatedTask);
            validateStartTime(updatedTask);
        }
        return tasks.get(taskId);
    }

    @Override
    public Subtask updateSubtask(int subtaskId, Subtask updatedSubtask) {
        if (subtasks.containsKey(subtaskId)) {
            updatedSubtask.setId(subtaskId);
            updatedSubtask.setTaskType(TaskType.SUBTASK);
            updatedSubtask.setStartTime(subtasks.get(subtaskId).getStartTime());
            updatedSubtask.setDurationMinutes(subtasks.get(subtaskId).getDurationMinutes());
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            epics.get(subtasks.get(subtaskId).getEpicId()).removeSubtaskId(subtaskId);
            epics.get(subtasks.get(subtaskId).getEpicId()).addSubtaskId(subtaskId);
            syncEpic(updatedSubtask.getEpicId());
            validateStartTime(updatedSubtask);
        }
        return subtasks.get(subtaskId);
    }

    @Override
    public Epic updateEpic(int epicId, Epic updatedEpic) {
        if (epics.containsKey(epicId)) {
            updatedEpic.setId(epicId);
            updatedEpic.setTaskType(TaskType.EPIC);
            updatedEpic.setStartTime(epics.get(epicId).getStartTime());
            updatedEpic.setDurationMinutes(epics.get(epicId).getDurationMinutes());
            epics.put(updatedEpic.getId(), updatedEpic);
        }
        return epics.get(epicId);
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {
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
    public void deleteAnyTask(int anyTaskId) {
        historyManager.remove(anyTaskId);
        if (tasks.containsKey(anyTaskId)) {
            tasks.remove(anyTaskId);
        } else if (subtasks.containsKey(anyTaskId)) {
            epics.get(subtasks.get(anyTaskId).getEpicId()).removeSubtaskId(anyTaskId);

            if (epics.get(subtasks.get(anyTaskId).getEpicId()).getSubtaskIds().size() != 0) {
                syncEpic(subtasks.get(anyTaskId).getEpicId());
            }

            subtasks.remove(anyTaskId);
        } else if (epics.containsKey(anyTaskId)) {
            List<Integer> subtaskIds = epics.get(anyTaskId).getSubtaskIds();
            epics.remove(anyTaskId);
            if (subtaskIds.size() != 0) {
                for (Integer subtaskId : subtaskIds) {
                    historyManager.remove(subtaskId);
                }
            }
        } else {
            System.out.println("Такой задачи пока нет");
        }
    }

    @Override
    public Task getAnyTask(int anyTaskId) {
        if (tasks.containsKey(anyTaskId)) {
            historyManager.add(tasks.get(anyTaskId));
            return tasks.get(anyTaskId);
        } else if (subtasks.containsKey(anyTaskId)) {
            historyManager.add(subtasks.get(anyTaskId));
            return subtasks.get(anyTaskId);
        } else if (epics.containsKey(anyTaskId)) {
            historyManager.add(epics.get(anyTaskId));
            return epics.get(anyTaskId);
        } else {
            System.out.println("Такой задачи пока нет");
        }
        return null;
    }

    private void syncEpic(int epicId) {
        if (epics.get(epicId).getSubtaskIds().size() != 0) {
            syncEpicStatus(epicId);
            syncEpicStartTime(epicId);
            syncEpicDuration(epicId);
            syncEpicEndTime(epicId);
        }
    }

    private void syncEpicStatus(int epicId) {
        int countNew = 0;
        int countDone = 0;
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();

        if (epics.get(epicId).getSubtaskIds().size() == 0) {
            epics.get(epicId).setStatus(TaskStatus.NEW);
        } else {
            for (Integer subtaskId : subtaskIds) {
                switch (subtasks.get(subtaskId).getStatus()) {
                    case NEW:
                        countNew++;
                        break;
                    case DONE:
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


    private void syncEpicDuration(int epicId) {
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        List<Integer> subtasksDurationMinutes = new ArrayList<>();

        for (Integer subtaskId : subtaskIds) {
            subtasksDurationMinutes.add(subtasks.get(subtaskId).getDurationMinutes());
        }

        epics.get(epicId).setDurationMinutes(subtasksDurationMinutes);
    }

    private void syncEpicStartTime(int epicId) {
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        List<LocalDateTime> subtasksStartTimes = new ArrayList<>();

        for (Integer subtaskId : subtaskIds) {
            subtasksStartTimes.add(subtasks.get(subtaskId).getStartTime());
        }

        epics.get(epicId).setStartTime(subtasksStartTimes);
    }

    private void syncEpicEndTime(int epicId) {
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        List<LocalDateTime> subtasksEndTimes = new ArrayList<>();

        for (Integer subtaskId : subtaskIds) {
            subtasksEndTimes.add(subtasks.get(subtaskId).getEndTime());
        }

        epics.get(epicId).setEndTime(subtasksEndTimes);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedTasksWithNullStartDateElements = new LinkedList<>();
        prioritizedTasksWithNullStartDateElements.addAll(prioritizedTasks);

        for (Task task : tasks.values()) {
            if (task.getStartTime() == null) {
                prioritizedTasksWithNullStartDateElements.add(task);
            }
        }

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() == null) {
                prioritizedTasksWithNullStartDateElements.add(subtask);
            }
        }

        return prioritizedTasksWithNullStartDateElements;
    }

    private void validateStartTime(Task task) {
        try {
            if (task.getStartTime() != null) {
                for (Task prioritizedTask : prioritizedTasks) {
                    if (task.getStartTime().isEqual(prioritizedTask.getStartTime())) {
                        throw new AddTaskException("Дата старта задачи " + task.getId() + " пересекается с другими задачами.");
                    }
                }
                prioritizedTasks.add(task);
            }
        } catch (AddTaskException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}