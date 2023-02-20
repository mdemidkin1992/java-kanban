package managers.tasks;

import managers.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Arrays;

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
        epic = new Epic("Test Epic name", "Test Epic description", TaskStatus.NEW);
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
        int duration = 30;
        LocalDateTime startTime = LocalDateTime.of(2023, Month.FEBRUARY, 20, 9, 0);
        epic.setEpicEndTime(startTime.plusMinutes(duration));
        LocalDateTime expectedEndTime = startTime.plusMinutes(duration);

        assertEquals(expectedEndTime, epic.getEndTime(), "Время окончания не совпадает.");
    }

    @Test
    public void shouldTransformToString() {
        assertEquals(expectedString, epic.toString(), "Вывод строки не совпадает с ожидаемым значением.");
    }
}