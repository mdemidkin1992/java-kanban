package managers.tasks;

import managers.enums.TaskStatus;
import managers.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Maxim Demidkin
 * @Date 16.02.2023
 */

class TaskTest {

    private static Task task;
    private static String expectedString;

    // private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm");

    @BeforeEach
    public void beforeEach() {
        task = new Task("Test Task1 name"
                , "Test Task1 description"
                , TaskStatus.IN_PROGRESS
                , generateDurationMinutes()
                , generateStartTime());
        expectedString = task.toString();
    }

    @Test
    public void shouldSetId() {
        task.setId(1);
        assertNotNull(task.getId(), "Переданный id не был присвоен");
        assertEquals(1, task.getId(), "Переданный id не совпадает с ожидаемым");
    }

    @Test
    public void shouldSetTaskType() {
        task.setTaskType(TaskType.TASK);
        assertNotNull(task.getTaskType(), "Тип задачи не был присвоен");
        assertEquals(TaskType.TASK, task.getTaskType(), "Тип задачи не совпадает с ожиданием");
    }

    @Test
    public void shouldSetTaskStatus() {
        task.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus(), "Статус задачи не был изменен");
        assertNotNull(task.getStatus(), "Статус задачи не был присвоен");
        assertEquals(TaskStatus.DONE, task.getStatus(), "Статус задачи не совпадает с ожиданием");
    }

    @Test
    public void shouldTransformToString() {
        assertEquals(expectedString, task.toString(), "Вывод строки не совпадает с ожидаемым значением.");
    }

    @Test
    public void shouldCompareTasks() {
        Task newTask = new Task(task.name, task.description, task.status, task.durationMinutes, task.startTime);
        assertEquals(task, newTask, "Задачи не равны");
        assertEquals(task.hashCode(), newTask.hashCode(), "Hashcode задач не равны");
    }

    @Test
    public void shouldSetDurationTime() {
        task.setDurationMinutes(30);
        assertEquals(30, task.getDurationMinutes(), "Продолжительность не совпадает.");
    }

    @Test
    public void shouldSetStartTime() {
        LocalDateTime randomDate = LocalDateTime.of(2023, Month.FEBRUARY, 20, 9, 0);
        task.setStartTime(randomDate);
        assertEquals(randomDate, task.getStartTime(), "Время старта не совпадает.");
    }

    @Test
    public void shouldGetEndTime() {
        int duration = 30;
        LocalDateTime startTime = LocalDateTime.of(2023, Month.FEBRUARY, 20, 9, 0);
        task.setDurationMinutes(duration);
        task.setStartTime(startTime);
        LocalDateTime expectedEndTime = startTime.plusMinutes(duration);

        assertEquals(expectedEndTime, task.getEndTime(), "Время окончания не совпадает.");
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