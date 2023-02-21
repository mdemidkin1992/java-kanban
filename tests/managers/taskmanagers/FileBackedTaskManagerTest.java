package managers.taskmanagers;

import managers.enums.TaskStatus;
import managers.tasks.Epic;
import managers.tasks.Subtask;
import managers.tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.Random;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private static final File file = new File("testTaskManagerFile.csv");

    public TaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    public void shouldSaveAndLoadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2, generateDurationMinutes(), generateStartTime());

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
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2, generateDurationMinutes(), generateStartTime());

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
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        assertDoesNotThrow(() -> fileBackedTaskManager.addTask(task));
    }

    private LocalDateTime generateStartTime() {
        Random random = new Random();
        return LocalDateTime.of(2023
                , Month.FEBRUARY
                , random.nextInt(28 - 20) + 20
                , random.nextInt(18 - 9) + 9
                , 0
                , 0);
    }

    private int generateDurationMinutes() {
        int[] possibleDuration = {30, 60, 90};
        Random random = new Random();
        return possibleDuration[(int) (random.nextDouble() * possibleDuration.length)];
    }
}