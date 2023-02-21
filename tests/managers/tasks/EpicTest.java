package managers.tasks;

import managers.enums.TaskStatus;
import managers.taskmanagers.TaskManager;
import managers.utilities.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Maxim Demidkin
 * @Date 16.02.2023
 */

class EpicTest {

    private static Epic epic;
    private static String expectedString;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("Test Epic name"
                , "Test Epic description"
                , TaskStatus.NEW
                , generateDurationMinutes()
                , generateStartTime());
        expectedString = epic.toString();
    }

    @Test
    public void shouldAddSubtaskId() {
        epic.addSubtaskId(2);
        epic.addSubtaskId(3);

        assertNotNull(epic.getSubtaskIds(), "Подзадачи не найдены");
        assertEquals(2, epic.getSubtaskIds().size(), "Размер списка не совпадает");
        assertEquals(Arrays.asList(2, 3), epic.getSubtaskIds(), "Списки подзадач не совпадают");
    }

    @Test
    public void shouldRemoveSubtaskId() {
        epic.addSubtaskId(2);
        epic.addSubtaskId(3);
        assertEquals(2, epic.getSubtaskIds().size(), "Размер списка не совпадает");

        epic.removeSubtaskId(2);
        assertEquals(1, epic.getSubtaskIds().size(), "Размер списка не совпадает");

        epic.removeSubtaskId(3);
        assertEquals(0, epic.getSubtaskIds().size(), "Список не пустой после всех обновлений");
    }

    @Test
    public void shouldGetSubtaskIds() {
        List<Integer> givenSubtaskIds = Arrays.asList(2, 3);
        epic.addSubtaskId(2);
        epic.addSubtaskId(3);

        assertEquals(givenSubtaskIds, epic.getSubtaskIds(), "Списки не совпадают");
    }

    @Test
    public void shouldGetEndTime() {
//        int duration = 30;
//        LocalDateTime startTime = LocalDateTime.of(2023, Month.FEBRUARY, 20, 9, 0);
//        epic.setEndTime(startTime.plusMinutes(duration));
        TaskManager taskManager = Managers.getDefault();
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Подзадача 1", TaskStatus.NEW, 1, 30, LocalDateTime.of(2023,Month.FEBRUARY, 8,9,0));
        Subtask subtask2 = new Subtask("Подзадача 2", "Подзадача 2", TaskStatus.IN_PROGRESS, 1, 30, LocalDateTime.of(2023,Month.FEBRUARY, 20,9,0));
        Subtask subtask3 = new Subtask("Подзадача 3", "Подзадача 3", TaskStatus.DONE, 1, 30, LocalDateTime.of(2023,Month.FEBRUARY, 5,9,0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        LocalDateTime expectedEndTime = taskManager.getAllSubtasks().values().stream()
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        assertEquals(expectedEndTime, epic.getEndTime(), "Время окончания не совпадает.");
    }

    @Test
    public void shouldTransformToString() {
        assertEquals(expectedString, epic.toString(), "Вывод строки не совпадает с ожидаемым значением.");
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