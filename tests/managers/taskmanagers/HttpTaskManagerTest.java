package managers.taskmanagers;

import com.google.gson.Gson;
import managers.utilities.Managers;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Maxim Demidkin
 * @Date 01.03.2023
 */

class HttpTaskManagerTest {
    private HttpTaskManager taskManager;
    private final static String URI = "http://localhost:8078/register";
    private Gson gson;

    @BeforeEach
    public void init() throws IOException, InterruptedException {
        gson = Managers.getGson();
        taskManager = new HttpTaskManager(URI);
        taskManager.loadFromFile(new File("taskManagerFile.csv"));
        taskManager.save();
    }

    @Test
    public void shouldLoadTasksFromServer() throws IOException, InterruptedException {
        final String actualTasks = gson.toJson(taskManager.getAllTasks());
        final String actualSubtasks = gson.toJson(taskManager.getAllSubtasks());
        final String actualEpics = gson.toJson(taskManager.getAllEpics());
        final String actualHistory = gson.toJson(taskManager.getHistory());
        final String actualPriorityTasks = gson.toJson(taskManager.getPrioritizedTasks());

        assertEquals(actualTasks, taskManager.load("task"));
        assertEquals(actualSubtasks, taskManager.load("subtask"));
        assertEquals(actualEpics, taskManager.load("epic"));
        assertEquals(actualHistory, taskManager.load("history"));
        assertEquals(actualPriorityTasks, taskManager.load("prioritised_tasks"));

        System.out.println(actualHistory);
        System.out.println(taskManager.getPrioritizedTasks());
    }

    @Test
    public void shouldAddNewTasksToServer() {

    }
}