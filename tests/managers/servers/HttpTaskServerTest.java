package managers.servers;

import com.google.gson.*;
import managers.enums.TaskStatus;
import managers.tasks.Epic;
import managers.tasks.Subtask;
import managers.tasks.Task;
import managers.utilities.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Maxim Demidkin
 * @Date 28.02.2023
 */

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private final Gson gson = Managers.getGson();
    private Task task_1;
    private Task task_2;
    private Epic epic_1;
    private Subtask subtask_1;
    private Subtask subtask_2;

    @BeforeEach
    public void init() throws IOException {
        httpTaskServer = new HttpTaskServer();

        task_1 = new Task("Задача 1", "Задача 1", TaskStatus.NEW, 30, LocalDateTime.of(2023, Month.FEBRUARY, 23, 9, 0));
        task_2 = new Task("Задача 2", "Задача 2", TaskStatus.NEW, 45, LocalDateTime.of(2023, Month.FEBRUARY, 25, 12, 30));
        epic_1 = new Epic("Эпик 1", "Эпик 1", TaskStatus.NEW, 45, LocalDateTime.of(2023, Month.FEBRUARY, 22, 11, 30));
        subtask_1 = new Subtask("Подзадача 1", "Подзадача 1", TaskStatus.NEW, 3, 60, LocalDateTime.of(2023, Month.FEBRUARY, 22, 17, 30));
        subtask_2 = new Subtask("Подзадача 2", "Подзадача 2", TaskStatus.NEW, 3, 90, LocalDateTime.of(2023, Month.FEBRUARY, 21, 15, 30));

        httpTaskServer.start();
    }

    @AfterEach
    public void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    public void shouldAddTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/epic");
        URI uri_3 = URI.create("http://localhost:8080/tasks/subtask");
        URI uri_4 = URI.create("http://localhost:8080/tasks/unknownTaskType");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);
        String json_3 = gson.toJson(epic_1);
        String json_4 = gson.toJson(subtask_1);
        String json_5 = gson.toJson(subtask_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);
        final HttpRequest.BodyPublisher body_3 = HttpRequest.BodyPublishers.ofString(json_3);
        final HttpRequest.BodyPublisher body_4 = HttpRequest.BodyPublishers.ofString(json_4);
        final HttpRequest.BodyPublisher body_5 = HttpRequest.BodyPublishers.ofString(json_5);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();
        HttpRequest requestPOST_3 = HttpRequest.newBuilder().uri(uri_2).POST(body_3).build();
        HttpRequest requestPOST_4 = HttpRequest.newBuilder().uri(uri_3).POST(body_4).build();
        HttpRequest requestPOST_5 = HttpRequest.newBuilder().uri(uri_3).POST(body_5).build();
        HttpRequest requestPOST_6 = HttpRequest.newBuilder().uri(uri_4).POST(body_5).build();

        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_3 = client.send(requestPOST_3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_4 = client.send(requestPOST_4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_5 = client.send(requestPOST_5, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_6 = client.send(requestPOST_6, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(200, responsePOST_3.statusCode());
        assertEquals(200, responsePOST_4.statusCode());
        assertEquals(200, responsePOST_5.statusCode());
        assertEquals(405, responsePOST_6.statusCode());
        assertEquals(2, httpTaskServer.taskManager.getAllTasks().size());
        assertEquals(1, httpTaskServer.taskManager.getAllEpics().size());
        assertEquals(2, httpTaskServer.taskManager.getAllSubtasks().size());
    }

    @Test
    public void shouldGetTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/epic");
        URI uri_3 = URI.create("http://localhost:8080/tasks/subtask");
        URI uri_4 = URI.create("http://localhost:8080/tasks/task/?id=1");
        URI uri_5 = URI.create("http://localhost:8080/tasks/epic/?id=3");
        URI uri_6 = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        URI uri_7 = URI.create("http://localhost:8080/tasks/undefinedTaskType/?id=4");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);
        String json_3 = gson.toJson(epic_1);
        String json_4 = gson.toJson(subtask_1);
        String json_5 = gson.toJson(subtask_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);
        final HttpRequest.BodyPublisher body_3 = HttpRequest.BodyPublishers.ofString(json_3);
        final HttpRequest.BodyPublisher body_4 = HttpRequest.BodyPublishers.ofString(json_4);
        final HttpRequest.BodyPublisher body_5 = HttpRequest.BodyPublishers.ofString(json_5);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();
        HttpRequest requestPOST_3 = HttpRequest.newBuilder().uri(uri_2).POST(body_3).build();
        HttpRequest requestPOST_4 = HttpRequest.newBuilder().uri(uri_3).POST(body_4).build();
        HttpRequest requestPOST_5 = HttpRequest.newBuilder().uri(uri_3).POST(body_5).build();

        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_3 = client.send(requestPOST_3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_4 = client.send(requestPOST_4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_5 = client.send(requestPOST_5, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGET_1 = HttpRequest.newBuilder().uri(uri_4).GET().build();
        HttpRequest requestGET_2 = HttpRequest.newBuilder().uri(uri_5).GET().build();
        HttpRequest requestGET_3 = HttpRequest.newBuilder().uri(uri_6).GET().build();
        HttpRequest requestGET_4 = HttpRequest.newBuilder().uri(uri_7).GET().build();

        HttpResponse<String> responseGET_1 = client.send(requestGET_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_2 = client.send(requestGET_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_3 = client.send(requestGET_3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_4 = client.send(requestGET_4, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(200, responsePOST_3.statusCode());
        assertEquals(200, responsePOST_4.statusCode());
        assertEquals(200, responsePOST_5.statusCode());
        assertEquals(200, responseGET_1.statusCode());
        assertEquals(200, responseGET_2.statusCode());
        assertEquals(200, responseGET_3.statusCode());
        assertEquals(405, responseGET_4.statusCode());

        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(1)), responseGET_1.body());
        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(3)), responseGET_2.body());
        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(4)), responseGET_3.body());
        assertEquals(3, httpTaskServer.taskManager.getHistory().size());
    }

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/epic");
        URI uri_3 = URI.create("http://localhost:8080/tasks/subtask");
        URI uri_4 = URI.create("http://localhost:8080/tasks/unknownTaskType");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();
        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGET_1 = HttpRequest.newBuilder().uri(uri_1).GET().build();
        HttpRequest requestGET_2 = HttpRequest.newBuilder().uri(uri_2).GET().build();
        HttpRequest requestGET_3 = HttpRequest.newBuilder().uri(uri_3).GET().build();
        HttpRequest requestGET_4 = HttpRequest.newBuilder().uri(uri_4).GET().build();

        HttpResponse<String> responseGET_1 = client.send(requestGET_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_2 = client.send(requestGET_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_3 = client.send(requestGET_3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_4 = client.send(requestGET_4, HttpResponse.BodyHandlers.ofString());

        JsonObject actual = JsonParser.parseString(responseGET_1.body()).getAsJsonObject();

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(200, responseGET_1.statusCode());
        assertEquals(200, responseGET_2.statusCode());
        assertEquals(200, responseGET_3.statusCode());
        assertEquals(405, responseGET_4.statusCode());
        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(1)), actual.get("1").toString());
        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(2)), actual.get("2").toString());
        assertEquals(2, httpTaskServer.taskManager.getHistory().size());
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/task?id=1");
        URI uri_3 = URI.create("http://localhost:8080/tasks/unknownTaskType?id=1");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();

        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestDELETE_1 = HttpRequest.newBuilder().uri(uri_2).DELETE().build();
        HttpRequest requestDELETE_2 = HttpRequest.newBuilder().uri(uri_3).DELETE().build();

        HttpResponse<String> responseDELETE_1 = client.send(requestDELETE_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseDELETE_2 = client.send(requestDELETE_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(200, responseDELETE_1.statusCode());
        assertEquals(405, responseDELETE_2.statusCode());
        assertEquals(1, httpTaskServer.taskManager.getAllTasks().size());
    }

    @Test
    public void shouldDeleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/subtask");
        URI uri_3 = URI.create("http://localhost:8080/tasks/epic");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();
        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestDELETE_1 = HttpRequest.newBuilder().uri(uri_1).DELETE().build();
        HttpRequest requestDELETE_2 = HttpRequest.newBuilder().uri(uri_2).DELETE().build();
        HttpRequest requestDELETE_3 = HttpRequest.newBuilder().uri(uri_3).DELETE().build();

        HttpResponse<String> responseDELETE_1 = client.send(requestDELETE_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseDELETE_2 = client.send(requestDELETE_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseDELETE_3 = client.send(requestDELETE_3, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(200, responseDELETE_1.statusCode());
        assertEquals(200, responseDELETE_2.statusCode());
        assertEquals(200, responseDELETE_3.statusCode());
        assertEquals(0, httpTaskServer.taskManager.getAllTasks().size());
    }

    @Test
    public void shouldSend405ResponseCodeWithIdIncorrectWhenGet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/task?id=3");
        URI uri_3 = URI.create("http://localhost:8080/tasks/task?id=foo");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();

        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGET_1 = HttpRequest.newBuilder().uri(uri_2).GET().build();
        HttpRequest requestGET_2 = HttpRequest.newBuilder().uri(uri_3).GET().build();

        HttpResponse<String> responseGET_1 = client.send(requestGET_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_2 = client.send(requestGET_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(405, responseGET_1.statusCode());
        assertEquals(405, responseGET_2.statusCode());
        assertEquals(0, httpTaskServer.taskManager.getHistory().size());
    }

    @Test
    public void shouldSend405ResponseCodeWithIdIncorrectWhenDelete() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_3 = URI.create("http://localhost:8080/tasks/task?id=foo");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();

        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestDELETE_2 = HttpRequest.newBuilder().uri(uri_3).DELETE().build();

        HttpResponse<String> responseDELETE_2 = client.send(requestDELETE_2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(405, responseDELETE_2.statusCode());
    }

    @Test
    public void shouldSend405ResponseCodeHttpMethodIsIncorrect() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        String json_1 = gson.toJson(task_1);
        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        HttpRequest requestPUT = HttpRequest.newBuilder().uri(uri_1).PUT(body_1).build();
        HttpResponse<String> responsePUT = client.send(requestPUT, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, responsePUT.statusCode());
    }

    @Test
    public void shouldThrowExceptionWhenUriIsIncorrect() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri_1 = URI.create("http://localhost:8110/subtasks/epics");
        String json_1 = gson.toJson(task_1);
        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        HttpRequest requestPUT = HttpRequest.newBuilder().uri(uri_1).PUT(body_1).build();

        assertThrows(IOException.class, () -> client.send(requestPUT, HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    public void shouldReturnHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/epic");
        URI uri_3 = URI.create("http://localhost:8080/tasks/subtask");
        URI uri_4 = URI.create("http://localhost:8080/tasks/task/?id=1");
        URI uri_5 = URI.create("http://localhost:8080/tasks/epic/?id=3");
        URI uri_6 = URI.create("http://localhost:8080/tasks/subtask/?id=4");
        URI uri_7 = URI.create("http://localhost:8080/tasks/history");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);
        String json_3 = gson.toJson(epic_1);
        String json_4 = gson.toJson(subtask_1);
        String json_5 = gson.toJson(subtask_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);
        final HttpRequest.BodyPublisher body_3 = HttpRequest.BodyPublishers.ofString(json_3);
        final HttpRequest.BodyPublisher body_4 = HttpRequest.BodyPublishers.ofString(json_4);
        final HttpRequest.BodyPublisher body_5 = HttpRequest.BodyPublishers.ofString(json_5);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();
        HttpRequest requestPOST_3 = HttpRequest.newBuilder().uri(uri_2).POST(body_3).build();
        HttpRequest requestPOST_4 = HttpRequest.newBuilder().uri(uri_3).POST(body_4).build();
        HttpRequest requestPOST_5 = HttpRequest.newBuilder().uri(uri_3).POST(body_5).build();

        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_3 = client.send(requestPOST_3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_4 = client.send(requestPOST_4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_5 = client.send(requestPOST_5, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGET_1 = HttpRequest.newBuilder().uri(uri_4).GET().build();
        HttpRequest requestGET_2 = HttpRequest.newBuilder().uri(uri_5).GET().build();
        HttpRequest requestGET_3 = HttpRequest.newBuilder().uri(uri_6).GET().build();
        HttpRequest requestGET_4 = HttpRequest.newBuilder().uri(uri_7).GET().build();

        HttpResponse<String> responseGET_1 = client.send(requestGET_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_2 = client.send(requestGET_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_3 = client.send(requestGET_3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseGET_4 = client.send(requestGET_4, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(200, responsePOST_3.statusCode());
        assertEquals(200, responsePOST_4.statusCode());
        assertEquals(200, responsePOST_5.statusCode());
        assertEquals(200, responseGET_1.statusCode());
        assertEquals(200, responseGET_2.statusCode());
        assertEquals(200, responseGET_3.statusCode());
        assertEquals(200, responseGET_4.statusCode());

        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(1)), responseGET_1.body());
        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(3)), responseGET_2.body());
        assertEquals(gson.toJson(httpTaskServer.taskManager.getAnyTask(4)), responseGET_3.body());
        assertEquals(gson.toJson(httpTaskServer.taskManager.getHistory()), responseGET_4.body());
    }

    @Test
    public void shouldReturnPrioritisedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri_1 = URI.create("http://localhost:8080/tasks/task");
        URI uri_2 = URI.create("http://localhost:8080/tasks/epic");
        URI uri_3 = URI.create("http://localhost:8080/tasks/subtask");
        URI uri_4 = URI.create("http://localhost:8080/tasks");

        String json_1 = gson.toJson(task_1);
        String json_2 = gson.toJson(task_2);
        String json_3 = gson.toJson(epic_1);
        String json_4 = gson.toJson(subtask_1);
        String json_5 = gson.toJson(subtask_2);

        final HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(json_1);
        final HttpRequest.BodyPublisher body_2 = HttpRequest.BodyPublishers.ofString(json_2);
        final HttpRequest.BodyPublisher body_3 = HttpRequest.BodyPublishers.ofString(json_3);
        final HttpRequest.BodyPublisher body_4 = HttpRequest.BodyPublishers.ofString(json_4);
        final HttpRequest.BodyPublisher body_5 = HttpRequest.BodyPublishers.ofString(json_5);

        HttpRequest requestPOST_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();
        HttpRequest requestPOST_2 = HttpRequest.newBuilder().uri(uri_1).POST(body_2).build();
        HttpRequest requestPOST_3 = HttpRequest.newBuilder().uri(uri_2).POST(body_3).build();
        HttpRequest requestPOST_4 = HttpRequest.newBuilder().uri(uri_3).POST(body_4).build();
        HttpRequest requestPOST_5 = HttpRequest.newBuilder().uri(uri_3).POST(body_5).build();

        HttpResponse<String> responsePOST_1 = client.send(requestPOST_1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_2 = client.send(requestPOST_2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_3 = client.send(requestPOST_3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_4 = client.send(requestPOST_4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responsePOST_5 = client.send(requestPOST_5, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGET_1 = HttpRequest.newBuilder().uri(uri_4).GET().build();

        HttpResponse<String> responseGET_1 = client.send(requestGET_1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePOST_1.statusCode());
        assertEquals(200, responsePOST_2.statusCode());
        assertEquals(200, responsePOST_3.statusCode());
        assertEquals(200, responsePOST_4.statusCode());
        assertEquals(200, responsePOST_5.statusCode());
        assertEquals(200, responseGET_1.statusCode());

        assertEquals(gson.toJson(httpTaskServer.taskManager.getPrioritizedTasks()), responseGET_1.body());
    }
}