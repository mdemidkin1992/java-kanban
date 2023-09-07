package managers.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.historymanagers.HistoryManager;
import managers.historymanagers.InMemoryHistoryManager;
import managers.taskmanagers.FileBackedTaskManager;
import managers.taskmanagers.HttpTaskManager;
import managers.taskmanagers.TaskManager;

import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078/register");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return new FileBackedTaskManager("taskManagerFile.csv");
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

}
