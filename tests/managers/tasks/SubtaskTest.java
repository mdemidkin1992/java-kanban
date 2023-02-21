package managers.tasks;

import managers.enums.TaskStatus;
import managers.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Maxim Demidkin
 * @Date 16.02.2023
 */

class SubtaskTest {

    private static Subtask subtask;
    private static String expectedString;

    @BeforeEach
    public void beforeEach() {
        subtask = new Subtask("Test Subtask1 name"
                , "Test Subtask1 description"
                , TaskStatus.IN_PROGRESS
                , 2
                , generateDurationMinutes()
                , generateStartTime());
        subtask.setId(1);
        subtask.setTaskType(TaskType.SUBTASK);
        expectedString = subtask.toString();
    }

    @Test
    public void shouldGetEpicId() {
        assertEquals(2, subtask.getEpicId(), "Номер эпика не совпадает");
    }

    @Test
    public void shouldTransformToString() {
        assertEquals(expectedString, subtask.toString(), "Вывод строки не совпадает с ожидаемым значением");
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