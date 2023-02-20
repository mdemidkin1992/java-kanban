package managers.taskmanagers;

import managers.enums.TaskStatus;
import managers.tasks.Epic;
import managers.tasks.Subtask;
import managers.tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Collections;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private static final File file = new File("testTaskManagerFile.csv");

    public TaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    public void shouldSaveAndLoadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW);
        final Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW);
        final Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2);

        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addEpic(epic);
        fileBackedTaskManager.addSubtask(subtask);

        fileBackedTaskManager.getAnyTask(task.getId());
        fileBackedTaskManager.getAnyTask(subtask.getId());
        fileBackedTaskManager.getAnyTask(epic.getId());

        FileBackedTaskManager newFileBackedTaskManager = new FileBackedTaskManager(file);
        newFileBackedTaskManager.loadFromFile(file);

        assertEquals(fileBackedTaskManager.getAllTasks(), newFileBackedTaskManager.getAllTasks(), "Списки не совпадают.");
        assertEquals(fileBackedTaskManager.getAllSubtasks(), newFileBackedTaskManager.getAllSubtasks(), "Списки не совпадают.");
        assertEquals(fileBackedTaskManager.getAllEpics(), newFileBackedTaskManager.getAllEpics(), "Списки не совпадают.");
        assertEquals(fileBackedTaskManager.getHistory(), newFileBackedTaskManager.getHistory(), "Списки не совпадают.");
    }

    @Test
    public void shouldNotThrowExceptionWhenLoadingFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW);
        final Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW);
        final Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2);

        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addEpic(epic);
        fileBackedTaskManager.addSubtask(subtask);

        fileBackedTaskManager.getAnyTask(task.getId());
        fileBackedTaskManager.getAnyTask(subtask.getId());
        fileBackedTaskManager.getAnyTask(epic.getId());

        file.delete();

        FileBackedTaskManager newFileBackedTaskManager = new FileBackedTaskManager(file);

        assertDoesNotThrow(() -> newFileBackedTaskManager.loadFromFile(file));
        assertEquals(Collections.emptyList(), newFileBackedTaskManager.getHistory());
        assertEquals(0, newFileBackedTaskManager.getAllTasks().values().size());
        assertEquals(0, newFileBackedTaskManager.getAllEpics().values().size());
        assertEquals(0, newFileBackedTaskManager.getAllSubtasks().values().size());
    }

    @Test
    public void shouldNotThrowExceptionWhenSavingToFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW);
        assertDoesNotThrow(() -> fileBackedTaskManager.addTask(task));
    }
}