package managers.taskmanagers;

import managers.enums.TaskStatus;
import managers.enums.TaskType;
import managers.exceptions.ManagerSaveException;
import managers.historymanagers.HistoryManager;
import managers.tasks.Epic;
import managers.tasks.Subtask;
import managers.tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String fileName;

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
    }

    protected void save() {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileName))) {
            printWriter.print("id,type,name,status,description,epic,duration,startTime");
            for (Task task : tasks.values()) {
                printWriter.print(task.toString());
            }
            for (Subtask subtask : subtasks.values()) {
                printWriter.print(subtask.toString());
            }
            for (Epic epic : epics.values()) {
                printWriter.print(epic.toString());
            }
            printWriter.println("\n");
            printWriter.print(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл");
        }
    }

    private String historyToString(HistoryManager manager) {
        StringBuilder historyLine = new StringBuilder();
        List<Task> history = manager.getHistory();
        for (int i = 0; i < history.size(); i++) {
            if (i != history.size() - 1) {
                historyLine.append(history.get(i).getId()).append(",");
            } else {
                historyLine.append(history.get(i).getId());
            }
        }
        return historyLine.toString();
    }

    private List<Integer> historyFromString(String value) {
        String[] taskIds = value.split(",");
        List<Integer> result = new ArrayList<>();
        for (String taskId : taskIds) {
            result.add(Integer.parseInt(taskId));
        }
        return result;
    }

    private void loadTasksFromFile(List<String> lines) {
        int breakLine = 0;
        if (!lines.isEmpty()) {
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).isEmpty()) {
                    breakLine = i;
                }
            }
            for (int i = 1; i < breakLine; i++) {
                fromString(lines.get(i));
            }
        }
    }

    private void loadHistoryFromFile(List<String> lines) {
        if (!lines.isEmpty()) {
            if (!lines.get(lines.size() - 1).isEmpty()) {
                List<Integer> taskIds = historyFromString(lines.get(lines.size() - 1));
                for (Integer taskId : taskIds) {
                    if (tasks.containsKey(taskId)) {
                        historyManager.add(tasks.get(taskId));
                    } else if (subtasks.containsKey(taskId)) {
                        historyManager.add(subtasks.get(taskId));
                    } else if (epics.containsKey(taskId)) {
                        historyManager.add(epics.get(taskId));
                    }
                }
            }
        }
    }

    private List<String> readFile(File file) {
        try {
            return Files.readAllLines(Path.of(file.toPath().toUri()));
        } catch (IOException e) {
            System.out.println("Файл не найден " + e.getMessage());
        }
        return Collections.emptyList();
    }

    protected void loadFromFile(File file) {
        loadTasksFromFile(readFile(file));
        loadHistoryFromFile(readFile(file));
    }

    private void fromString(String value) {
        String[] parts = value.split(",");
        TaskType taskType = TaskType.valueOf(parts[1]);
        switch (taskType) {
            case TASK: {
                Task task = new Task(parts[2]
                        , parts[4]
                        , TaskStatus.valueOf(parts[3])
                        , Integer.parseInt(parts[5])
                        , LocalDateTime.parse(parts[6]));
                task.setTaskType(TaskType.TASK);
                task.setId(Integer.parseInt(parts[0]));
                tasks.put(task.getId(), task);
                break;
            }
            case SUBTASK: {
                Subtask subtask = new Subtask(parts[2]
                        , parts[4]
                        , TaskStatus.valueOf(parts[3])
                        , Integer.parseInt(parts[5])
                        , Integer.parseInt(parts[6])
                        , LocalDateTime.parse(parts[7]));
                subtask.setTaskType(TaskType.SUBTASK);
                subtask.setId(Integer.parseInt(parts[0]));
                subtasks.put(subtask.getId(), subtask);
                break;
            }
            case EPIC: {
                Epic epic = new Epic(parts[2]
                        , parts[4]
                        , TaskStatus.valueOf(parts[3])
                        , Integer.parseInt(parts[5])
                        , LocalDateTime.parse(parts[6]));
                epic.setTaskType(TaskType.EPIC);
                epic.setId(Integer.parseInt(parts[0]));
                epics.put(epic.getId(), epic);
                break;
            }
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }


    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Map<Integer, Task> getAllTasks() {
        super.getAllTasks();
        save();
        return tasks;
    }

    @Override
    public Map<Integer, Subtask> getAllSubtasks() {
        super.getAllSubtasks();
        save();
        return subtasks;
    }

    @Override
    public Map<Integer, Epic> getAllEpics() {
        super.getAllEpics();
        save();
        return epics;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task updateTask(int taskId, Task updatedTask) {
        super.updateTask(taskId, updatedTask);
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(int subtaskId, Subtask updatedSubtask) {
        super.updateSubtask(subtaskId, updatedSubtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(int epicId, Epic updatedEpic) {
        super.updateEpic(epicId, updatedEpic);
        save();
        return updatedEpic;
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {
        List<Subtask> subtasksFromEpic = super.getSubtasksFromEpic(epicId);
        save();
        return subtasksFromEpic;
    }

    @Override
    public Task deleteAnyTask(int anyTaskId) {
        super.deleteAnyTask(anyTaskId);
        save();
        return null;
    }

    @Override
    public Task getAnyTask(int anyTaskId) {
        Task task = super.getAnyTask(anyTaskId);
        save();
        return task;
    }

}
