package managers.taskmanagers;

import com.google.gson.Gson;
import managers.clients.KVTaskClient;
import managers.exceptions.ManagerSaveException;
import managers.utilities.Managers;

import java.io.IOException;
import java.net.URI;

/**
 * @author Maxim Demidkin
 * @Date 01.03.2023
 */

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String urlName) throws IOException, InterruptedException {
        super(urlName);
        client = new KVTaskClient(URI.create(urlName));
        gson = Managers.getGson();
    }

    @Override
    protected void save() {
        try {
            client.put("task", gson.toJson(tasks));
            client.put("subtask", gson.toJson(subtasks));
            client.put("epic", gson.toJson(epics));
            client.put("history", gson.toJson(historyManager.getHistory()));
            client.put("prioritised_tasks", gson.toJson(prioritizedTasks));
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("Ошибка при записи на сервер.");
        }
    }

    protected String load(String key) throws IOException, InterruptedException {
        return client.load(key);
    }
}