package managers.taskmanagers;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    public TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}