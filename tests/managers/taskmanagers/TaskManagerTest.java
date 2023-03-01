package managers.taskmanagers;

import managers.enums.TaskStatus;
import managers.tasks.Epic;
import managers.tasks.Subtask;
import managers.tasks.Task;
import managers.utilities.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest {
    protected abstract TaskManager createTaskManager();
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    @Test
    public void shouldAddTask() {
        Task task = new Task("Task 1 name", "Task 1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int taskId = taskManager.addTask(task).getId();
        final Task savedTask = taskManager.getAllTasks().get(taskId);

        assertNotNull(savedTask, "Задачи не найдены.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final Map<Integer, Task> savedTasks = taskManager.getAllTasks();

        assertNotNull(savedTasks, "Задачи не возвращаются.");
        assertEquals(1, savedTasks.size(), "Неверное количество задач.");
        assertEquals(task, savedTasks.get(1), "Задачи не совпадают.");
    }

    @Test
    public void shouldAddEpic() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epicId = taskManager.addEpic(epic).getId();
        final Epic savedEpic = taskManager.getAllEpics().get(epicId);

        assertNotNull(savedEpic, "Задачи не найдены.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final Map<Integer, Epic> savedEpics = taskManager.getAllEpics();

        assertNotNull(savedEpics, "Задачи не возвращаются.");
        assertEquals(1, savedEpics.size(), "Неверное количество задач.");
        assertEquals(epic, savedEpics.get(1), "Задачи не совпадают.");
    }

    @Test
    public void shouldAddSubtask() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());

        final int epicId = taskManager.addEpic(epic).getId();
        final int subtaskId = taskManager.addSubtask(subtask).getId();
        final Epic savedEpic = taskManager.getAllEpics().get(epicId);
        final Subtask savedSubtask = taskManager.getAllSubtasks().get(subtaskId);

        assertNotNull(savedEpic, "Задачи не найдены.");
        assertNotNull(savedSubtask, "Подзадачи не найдены.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final Map<Integer, Epic> savedEpics = taskManager.getAllEpics();
        final Map<Integer, Subtask> savedSubtasks = taskManager.getAllSubtasks();

        assertNotNull(savedEpics, "Задачи не возвращаются.");
        assertNotNull(savedSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, savedEpics.size(), "Неверное количество задач.");
        assertEquals(1, savedSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        assertEquals(epicId, savedSubtask.getEpicId(), "Эпик не совпадает.");
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2, generateDurationMinutes(), generateStartTime());

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        assertEquals(1, taskManager.getAllTasks().size(), "Задачи не были добавлены.");
        assertEquals(1, taskManager.getAllEpics().size(), "Задачи не были добавлены.");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Задачи не были добавлены.");

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getAllTasks().size(), "Задачи не были удалены.");
        assertEquals(0, taskManager.getAllEpics().size(), "Задачи не были удалены.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Задачи не были удалены.");
    }

    @Test
    public void shouldUpdateTask() {
        Task oldTask = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        int taskId = taskManager.addTask(oldTask).getId();
        Task updatedTask = taskManager.updateTask(
                taskId,
                new Task("Test Task1 new name", "Test Task1 new description", TaskStatus.IN_PROGRESS, generateDurationMinutes(), generateStartTime())
        );

        assertEquals(updatedTask, taskManager.getAnyTask(taskId), "Задача не была обновлена.");
    }

    @Test
    public void shouldUpdateSubtask() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        taskManager.addEpic(epic);
        Subtask oldSubtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());
        final int subtaskId = taskManager.addSubtask(oldSubtask).getId();
        Subtask updatedSubtask = taskManager.updateSubtask(
                subtaskId,
                new Subtask("Test Subtask1 new name", "Test Subtask1 new description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime())
        );

        assertEquals(updatedSubtask, taskManager.getAnyTask(subtaskId), "Задача не была обновлена.");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic oldEpic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epicId = taskManager.addEpic(oldEpic).getId();
        Epic updatedEpic = taskManager.updateEpic(
                epicId,
                new Epic("Test Epic1 new name", "Test Epic1 new description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime())
        );

        assertEquals(updatedEpic, taskManager.getAllEpics().get(epicId), "Задача не была обновлена.");
    }

    @Test
    public void shouldUpdateEpicStatusWithAllSubtasksStatusNew() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epicId = taskManager.addEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, epicId, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.NEW, epicId, generateDurationMinutes(), generateStartTime());
        List<Integer> savedSubtaskIds = Arrays.asList(
                taskManager.addSubtask(subtask1).getId(),
                taskManager.addSubtask(subtask2).getId()
        );

        final Map<Integer, Epic> epics = taskManager.getAllEpics();
        final TaskStatus epicTaskStatus = epics.get(epicId).getStatus();

        assertEquals(2, savedSubtaskIds.size(), "Количество подзадач в эпике не совпадает.");
        assertEquals(TaskStatus.NEW, epicTaskStatus, "Статус эпика не совпадает.");
    }

    @Test
    public void shouldUpdateEpicStatusWithAllSubtasksStatusDone() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, 30, LocalDateTime.of(2023, 2, 20, 18, 0));
        final int epicId = taskManager.addEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.DONE, epicId, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.DONE, epicId, generateDurationMinutes(), generateStartTime());
        final List<Integer> savedSubtaskIds = Arrays.asList(
                taskManager.addSubtask(subtask1).getId(),
                taskManager.addSubtask(subtask2).getId()
        );

        final Map<Integer, Epic> epics = taskManager.getAllEpics();
        final TaskStatus epicTaskStatus = epics.get(epicId).getStatus();

        assertEquals(2, savedSubtaskIds.size(), "Количество подзадач в эпике не совпадает.");
        assertEquals(TaskStatus.DONE, epicTaskStatus, "Статус эпика не совпадает.");
    }

    @Test
    public void shouldUpdateEpicStatusWithSubtasksStatusNewAndDone() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epicId = taskManager.addEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, epicId, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.DONE, epicId, generateDurationMinutes(), generateStartTime());
        final List<Integer> savedSubtaskIds = Arrays.asList(
                taskManager.addSubtask(subtask1).getId(),
                taskManager.addSubtask(subtask2).getId()
        );

        final Map<Integer, Epic> epics = taskManager.getAllEpics();
        final TaskStatus epicTaskStatus = epics.get(epicId).getStatus();

        assertEquals(2, savedSubtaskIds.size(), "Количество подзадач в эпике не совпадает.");
        assertEquals(TaskStatus.IN_PROGRESS, epicTaskStatus, "Статус эпика не совпадает.");
    }

    @Test
    public void shouldUpdateEpicStatusWithAllSubtasksStatusInProgress() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epicId = taskManager.addEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.IN_PROGRESS, epicId, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.IN_PROGRESS, epicId, generateDurationMinutes(), generateStartTime());
        final List<Integer> savedSubtaskIds = Arrays.asList(
                taskManager.addSubtask(subtask1).getId(),
                taskManager.addSubtask(subtask2).getId()
        );

        final Map<Integer, Epic> epics = taskManager.getAllEpics();
        final TaskStatus epicTaskStatus = epics.get(epicId).getStatus();

        assertEquals(2, savedSubtaskIds.size(), "Количество подзадач в эпике не совпадает.");
        assertEquals(TaskStatus.IN_PROGRESS, epicTaskStatus, "Статус эпика не совпадает.");
    }

    @Test
    public void shouldUpdateEpicStatusWithNoSubtasks() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epicId = taskManager.addEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.IN_PROGRESS, epicId, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.IN_PROGRESS, epicId, generateDurationMinutes(), generateStartTime());
        taskManager.addSubtask(subtask1).getId();
        taskManager.addSubtask(subtask2).getId();

        final Map<Integer, Epic> epics = taskManager.getAllEpics();

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика не обновлен.");

        taskManager.deleteAnyTask(subtask1.getId());
        taskManager.deleteAnyTask(subtask2.getId());

        assertEquals(0, epics.get(epicId).getSubtaskIds().size(), "Список подзадач не пустой.");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика не обновлен.");
    }

    @Test
    public void shouldGetSubtasksFromEpic() {
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epicId = taskManager.addEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.IN_PROGRESS, epicId, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.IN_PROGRESS, epicId, generateDurationMinutes(), generateStartTime());
        final List<Integer> savedSubtaskIds = Arrays.asList(
                taskManager.addSubtask(subtask1).getId(),
                taskManager.addSubtask(subtask2).getId()
        );
        final List<Subtask> subtasksInEpic = taskManager.getSubtasksFromEpic(epicId);
        for (int i = 0; i < subtasksInEpic.size(); i++) {
            assertEquals(savedSubtaskIds.get(i), taskManager.getSubtasksFromEpic(epicId).get(i).getId(), "Списки задач не совпадают.");
        }
    }

    @Test
    public void shouldGetAnyTaskByIdWhenIdIsCorrect() {
        Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Epic epic = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.IN_PROGRESS, 2, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.IN_PROGRESS, 2, generateDurationMinutes(), generateStartTime());

        final int taskId = taskManager.addTask(task).getId();
        final int epicId = taskManager.addEpic(epic).getId();
        final int subtask1Id = taskManager.addSubtask(subtask1).getId();
        final int subtask2Id = taskManager.addSubtask(subtask2).getId();

        final Task savedTask = taskManager.getAnyTask(taskId);
        final Task savedEpic = taskManager.getAnyTask(epicId);
        final Task savedSubtask1 = taskManager.getAnyTask(subtask1Id);
        final Task savedSubtask2 = taskManager.getAnyTask(subtask2Id);

        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        assertEquals(subtask1, savedSubtask1, "Задачи не совпадают.");
        assertEquals(subtask2, savedSubtask2, "Задачи не совпадают.");
    }

    @Test
    public void shouldGetAnyTaskByIdWhenIdIsNotCorrect() {
        taskManager.addTask(new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime()));
        taskManager.addTask(new Task("Test Task2 name", "Test Task2 description", TaskStatus.IN_PROGRESS, generateDurationMinutes(), generateStartTime()));
        taskManager.addTask(new Task("Test Task3 name", "Test Task3 description", TaskStatus.DONE, generateDurationMinutes(), generateStartTime()));

        final Map<Integer, Task> tasks = taskManager.getAllTasks();
        final int incorrectId = tasks.size() + 1;

        assertNull(taskManager.getAnyTask(incorrectId), "Вывод работает некорректно.");
    }

    @Test
    public void shouldDeleteAnyTask() {
        Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int taskId = taskManager.addTask(task).getId();
        final Map<Integer, Task> tasks = taskManager.getAllTasks();
        taskManager.deleteAnyTask(taskId);

        assertEquals(0, tasks.size(), "Задачи не были удалены.");
    }

    @Test
    public void shouldDeleteAnySubtask() {
        Epic epic1 = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());

        final int epic1Id = taskManager.addEpic(epic1).getId();
        final int subtaskId = taskManager.addSubtask(subtask).getId();

        final Map<Integer, Subtask> subtasks = taskManager.getAllSubtasks();
        final Map<Integer, Epic> epics = taskManager.getAllEpics();

        taskManager.deleteAnyTask(subtaskId);
        assertEquals(0, subtasks.size(), "Задачи не были удалены.");
        taskManager.deleteAnyTask(epic1Id);
        assertEquals(0, epics.size(), "Задачи не были удалены.");
    }

    @Test
    public void shouldDeleteAnyEpicWithNoSubtasks() {
        Epic epic1 = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        final int epic1Id = taskManager.addEpic(epic1).getId();
        final Map<Integer, Epic> epics = taskManager.getAllEpics();

        taskManager.deleteAnyTask(epic1Id);
        assertEquals(0, epics.size(), "Задачи не были удалены.");
    }

    @Test
    public void shouldDeleteAnyEpicWithSubtasks() {
        Epic epic2 = new Epic("Test Epic2 name", "Test Epic2 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());

        final int epic2Id = taskManager.addEpic(epic2).getId();
        taskManager.addSubtask(subtask2).getId();

        final Map<Integer, Epic> epics = taskManager.getAllEpics();

        taskManager.deleteAnyTask(epic2Id);
        assertEquals(0, epics.size(), "Задачи не были удалены.");

    }

    @Test
    public void shouldDeleteAnyTaskAndDoNothingWhenIdIsIncorrect() {
        Task task = new Task("Test Task1 name", "Test Task1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Epic epic1 = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 2, generateDurationMinutes(), generateStartTime());
        Epic epic2 = new Epic("Test Epic2 name", "Test Epic2 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 4, generateDurationMinutes(), generateStartTime());

        taskManager.addTask(task).getId();
        taskManager.addEpic(epic1).getId();
        taskManager.addSubtask(subtask).getId();
        taskManager.addEpic(epic2).getId();
        taskManager.addSubtask(subtask2).getId();

        final Map<Integer, Task> tasks = taskManager.getAllTasks();
        final Map<Integer, Subtask> subtasks = taskManager.getAllSubtasks();
        final Map<Integer, Epic> epics = taskManager.getAllEpics();

        taskManager.deleteAnyTask(555);

        assertEquals(1, tasks.size(), "Кол-во задач не совпадает.");
        assertEquals(2, subtasks.size(), "Кол-во задач не совпадает.");
        assertEquals(2, epics.size(), "Кол-во задач не совпадает.");
    }

    @Test
    public void shouldGetPrioritizedTasks() {
        Task task1 = new Task("Задача 1", "Задача 1", TaskStatus.NEW, 30, LocalDateTime.of(2023,Month.FEBRUARY, 10,9,0));
        Task task2 = new Task("Задача 2", "Задача 2", TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Задача 3", "Задача 3", TaskStatus.DONE);
        Epic epic = new Epic("Задача 1", "Задача 1", TaskStatus.NEW, 30, LocalDateTime.of(2023,Month.FEBRUARY, 9,10,0));
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1", TaskStatus.NEW, 4, 30, LocalDateTime.of(2023,Month.FEBRUARY, 8,9,0));
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2", TaskStatus.IN_PROGRESS, 4, 30, LocalDateTime.of(2023,Month.FEBRUARY, 20,9,0));
        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3", TaskStatus.DONE, 4, 30, LocalDateTime.of(2023,Month.FEBRUARY, 5,9,0));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Task> expectedList = new LinkedList<>();
        expectedList.add(subtask3);
        expectedList.add(subtask1);
        expectedList.add(task1);
        expectedList.add(subtask2);
        expectedList.add(task2);
        expectedList.add(task3);

        assertEquals(expectedList, taskManager.getPrioritizedTasks(), "Списки не совпадают.");
    }

    @Test
    public void shouldAddSubtaskDurationsForEpic() {
        Epic epic1 = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());

        taskManager.addEpic(epic1);

        final int durationSubtask1 = taskManager.addSubtask(subtask1).getDurationMinutes();
        final int durationSubtask2 = taskManager.addSubtask(subtask2).getDurationMinutes();
        final int expectedEpicDuration = durationSubtask1 + durationSubtask2;

        assertEquals(expectedEpicDuration, epic1.getDurationMinutes(), "Продолжительность не совпадает.");
    }

    @Test
    public void shouldSetEpicStartTimeToMinOfSubtasksStartTimes() throws IOException, InterruptedException {
        TaskManager newTaskManager = Managers.getDefault();
        Epic epic1 = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());

        newTaskManager.addEpic(epic1);
        newTaskManager.addSubtask(subtask1);
        newTaskManager.addSubtask(subtask2);

        final LocalDateTime subtask1StartTime = subtask1.getStartTime();
        final LocalDateTime subtask2StartTime = subtask2.getStartTime();

        final LocalDateTime minStartTime = Stream.of(subtask1StartTime, subtask2StartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        assertTrue(minStartTime.isEqual(epic1.getStartTime()), "Время начала эпика неверное.");
    }

    @Test
    public void shouldSetEpicEndTimeToMaxOfSubtasksEndTime() throws IOException, InterruptedException {
        TaskManager newTaskManager = Managers.getDefault();
        Epic epic1 = new Epic("Test Epic1 name", "Test Epic1 description", TaskStatus.NEW, generateDurationMinutes(), generateStartTime());
        Subtask subtask1 = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());
        Subtask subtask2 = new Subtask("Test Subtask2 name", "Test Subtask2 description", TaskStatus.NEW, 1, generateDurationMinutes(), generateStartTime());

        newTaskManager.addEpic(epic1);
        newTaskManager.addSubtask(subtask1);
        newTaskManager.addSubtask(subtask2);

        final LocalDateTime subtask1EndTime = subtask1.getEndTime();
        final LocalDateTime subtask2EndTime = subtask2.getEndTime();

        final LocalDateTime maxEndTime = Stream.of(subtask1EndTime, subtask2EndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        assertTrue(maxEndTime.isEqual(epic1.getEndTime()), "Время окончания эпика неверное.");
    }

    @Test
    public void shouldNotThrow() {
        assertDoesNotThrow(() -> taskManager.addTask(new Task("Задача 1", "Задача 1", TaskStatus.NEW, generateDurationMinutes(), generateStartTime())));
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