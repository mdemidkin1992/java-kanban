package managers.historymanagers;

import managers.enums.TaskStatus;
import managers.taskmanagers.TaskManager;
import managers.tasks.Epic;
import managers.tasks.Subtask;
import managers.tasks.Task;
import managers.utilities.Managers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Maxim Demidkin
 * @Date 20.02.2023
 */

class InMemoryHistoryManagerTest {

    @Test
    public void shouldRemoveNodeHead() {

        TaskManager taskManager = Managers.getDefault();
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2, generateDurationMinutes(), generateStartTime());

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        taskManager.getAnyTask(task.getId());
        taskManager.getAnyTask(epic.getId());
        taskManager.getAnyTask(subtask.getId());

        taskManager.deleteAnyTask(task.getId());

        final List<Task> expectedHistory = List.of(epic, subtask);

        assertEquals(expectedHistory, taskManager.getHistory(), "Записи в истории не совпадают.");
    }

    @Test
    public void shouldRemoveNode() {
        TaskManager taskManager = Managers.getDefault();
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2, generateDurationMinutes(), generateStartTime());

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        taskManager.getAnyTask(task.getId());
        taskManager.getAnyTask(epic.getId());
        taskManager.getAnyTask(subtask.getId());

        taskManager.deleteAnyTask(epic.getId());

        final List<Task> expectedHistory = List.of(task);

        assertEquals(expectedHistory, taskManager.getHistory(), "Записи в истории не совпадают.");
    }

    @Test
    public void shouldRemoveNodeTail() {
        TaskManager taskManager = Managers.getDefault();
        final Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2, generateDurationMinutes(), generateStartTime());

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        taskManager.getAnyTask(task.getId());
        taskManager.getAnyTask(epic.getId());
        taskManager.getAnyTask(subtask.getId());

        taskManager.deleteAnyTask(subtask.getId());

        final List<Task> expectedHistory = List.of(task, epic);

        assertEquals(expectedHistory, taskManager.getHistory(), "Записи в истории не совпадают.");
    }

    @Test
    public void shouldNotAddEqualTasksToHistory() {
        TaskManager taskManager = Managers.getDefault();
        final Task task1 = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Task task2 = new Task("Test Task2 name", "Test Task2 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Task task3 = new Task("Test Task3 name", "Test Task3 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getAnyTask(task1.getId());
        taskManager.getAnyTask(task2.getId());
        taskManager.getAnyTask(task3.getId());
        taskManager.getAnyTask(task1.getId());

        assertEquals(3, taskManager.getHistory().size(), "Повторяющиеся задачи были записаны повторно.");
        assertEquals(List.of(task2, task3, task1), taskManager.getHistory(), "Записи в истории не совпадают.");
    }

    @Test
    public void shouldReturnEmptyHistory() {
        TaskManager taskManager = Managers.getDefault();
        final Task task1 = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Task task2 = new Task("Test Task2 name", "Test Task2 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final Task task3 = new Task("Test Task3 name", "Test Task3 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        assertEquals(0, taskManager.getHistory().size(), "История задач не пустая.");
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