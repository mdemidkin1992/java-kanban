package managers.tasks;

import managers.enums.TaskStatus;
import managers.enums.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        subtask = new Subtask("Test Subtask1 name", "Test Subtask1 description", TaskStatus.IN_PROGRESS, 2);
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
}