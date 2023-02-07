package managers;

import managers.enums.TaskStatus;
import managers.enums.TaskType;
import managers.exceptions.ManagerSaveException;
import managers.tasks.Task;
import managers.tasks.Subtask;
import managers.tasks.Epic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("taskManagerFile.csv");
        FileBackedTaskManager fileBackedTaskManager = loadFromFile(file);
        System.out.println(tasks.values());
        System.out.println(subtasks.values());
        System.out.println(epics.values());
        System.out.println(historyManager.getHistory());
    }

    private void save() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(file.getName());
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print("id,type,name,status,description,epic");
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

    private static String historyToString(HistoryManager manager) {
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

    private static List<Integer> historyFromString(String value) {
        String[] taskIds = value.split(",");
        List<Integer> result = new ArrayList<>();
        for (String taskId : taskIds) {
            result.add(Integer.parseInt(taskId));
        }
        return result;
    }

    private static void loadTasksFromFile(List<String> lines) {
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

    private static void loadHistoryFromFile(List<String> lines) {
        if (!lines.get(lines.size() - 1).isEmpty()) {
            List<Integer> taskIds = historyFromString(lines.get(lines.size() - 1));
            for (Integer taskId : taskIds) {
                if (tasks.containsKey(taskId)) {
                    historyManager.add(tasks.get(taskId));
                } else if (subtasks.containsKey(taskId)) {
                    historyManager.add(subtasks.get(taskId));
                } else if (epics.containsKey(taskId)) {
                    historyManager.add(epics.get(taskId));
                } else {
                    System.out.println("Ошибка");
                }
            }
        }
    }

    private static List<String> readFile(File file) throws IOException {
        return Files.readAllLines(Path.of(file.toPath().toUri()));
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.loadTasksFromFile(readFile(file));
        fileBackedTaskManager.loadHistoryFromFile(readFile(file));
        return fileBackedTaskManager;
    }

    private static void fromString(String value) {
        String[] parts = value.split(",");
        TaskType taskType = TaskType.valueOf(parts[1]);
        switch (taskType) {
            case TASK: {
                Task task = new Task(parts[2], parts[4], TaskStatus.valueOf(parts[3]));
                task.setTaskType(TaskType.TASK);
                task.setId(Integer.parseInt(parts[0]));
                tasks.put(task.getId(), task);
                break;
            }
            case SUBTASK: {
                Subtask subtask = new Subtask(parts[2], parts[4], TaskStatus.valueOf(parts[3]), Integer.parseInt(parts[5]));
                subtask.setTaskType(TaskType.SUBTASK);
                subtask.setId(Integer.parseInt(parts[0]));
                subtasks.put(subtask.getId(), subtask);
                break;
            }
            case EPIC: {
                Epic epic = new Epic(parts[2], parts[4], TaskStatus.valueOf(parts[3]));
                epic.setTaskType(TaskType.EPIC);
                epic.setId(Integer.parseInt(parts[0]));
                epics.put(epic.getId(), epic);
                break;
            }
        }
    }

    @Override
    public Task addTask(Task task) throws IOException {
        super.addTask(task);
        save();
        return task;
    }


    @Override
    public Epic addEpic(Epic epic) throws IOException {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) throws IOException {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteAllTasks() throws ManagerSaveException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task updateTask(Integer taskId, Task updatedTask) throws ManagerSaveException {
        super.updateTask(taskId, updatedTask);
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Integer subtaskId, Subtask updatedSubtask) throws ManagerSaveException {
        super.updateSubtask(subtaskId, updatedSubtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Integer epicId, Epic updatedEpic) throws ManagerSaveException {
        super.updateEpic(epicId, updatedEpic);
        save();
        return updatedEpic;
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(Integer epicId) throws ManagerSaveException {
        List<Subtask> subtasksFromEpic = super.getSubtasksFromEpic(epicId);
        save();
        return subtasksFromEpic;
    }

    @Override
    public void deleteAnyTask(Integer anyTaskId) throws ManagerSaveException {
        super.deleteAnyTask(anyTaskId);
        save();
    }

    @Override
    public Object getAnyTask(Integer anyTaskId) throws IOException {
        super.getAnyTask(anyTaskId);
        save();
        return tasks.get(anyTaskId);
    }
}
