package managers.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.taskmanagers.TaskManager;
import managers.tasks.Epic;
import managers.tasks.Subtask;
import managers.tasks.Task;
import managers.utilities.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author Maxim Demidkin
 * @Date 27.02.2023
 */

public class HttpTaskServer {

    private final static int PORT = 8080;
    private final Gson gson;
    protected final TaskManager taskManager;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        gson = Managers.getGson();
        taskManager = Managers.getFileBackedTaskManager();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange httpExchange) {
        try {
            final String requestMethod = httpExchange.getRequestMethod();
            final String requestPath = httpExchange.getRequestURI().getPath();
            final String requestQuery = httpExchange.getRequestURI().getQuery();

            if (requestPath.split("/").length > 2) {
                final String taskTypeIdentifier = requestPath.split("/")[2];

                final boolean isAcceptedTaskType = taskTypeIdentifier.equals("task")
                        || taskTypeIdentifier.equals("epic")
                        || taskTypeIdentifier.equals("subtask");

                switch (requestMethod) {
                    case "POST":
                        InputStream inputStream = httpExchange.getRequestBody();
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                        switch (taskTypeIdentifier) {
                            case "task":
                                Task newTask = gson.fromJson(body, Task.class);
                                taskManager.addTask(newTask);
                                System.out.println("Задача c id " + newTask.getId() + " была добавлена.");
                                httpExchange.sendResponseHeaders(200, 0);
                                break;
                            case "epic":
                                Epic newEpic = gson.fromJson(body, Epic.class);
                                taskManager.addEpic(newEpic);
                                System.out.println("Эпик c id " + newEpic.getId() + " был добавлен.");
                                httpExchange.sendResponseHeaders(200, 0);
                                break;
                            case "subtask":
                                Subtask newSubtask = gson.fromJson(body, Subtask.class);
                                taskManager.addSubtask(newSubtask);
                                System.out.println("Подзадача c id " + newSubtask.getId() + " была добавлена с эпиок id " + newSubtask.getEpicId() + ".");
                                httpExchange.sendResponseHeaders(200, 0);
                                break;
                            default:
                                System.out.println("Указан некорректный идентификатор задачи " + taskTypeIdentifier + ".");
                                httpExchange.sendResponseHeaders(405, 0);
                                break;
                        }

                        break;

                    case "GET":

                        if (requestQuery != null) {
                            String queryId = requestQuery.replaceFirst("id=", "");
                            int id = parseQueryId(queryId);

                            if (isAcceptedTaskType) {

                                if (id != -1) {
                                    Task viewedTask = taskManager.getAnyTask(id);
                                    if (viewedTask != null) {
                                        String response = gson.toJson(viewedTask);
                                        System.out.println("Извлекаем из менеджера задачу с id " + id + ".");
                                        sendText(httpExchange, response);
                                    } else {
                                        System.out.println("Задачи с id " + id + " не существует.");
                                        httpExchange.sendResponseHeaders(405, 0);
                                    }
                                } else {
                                    System.out.println("Получен некорректный id " + id + ".");
                                    httpExchange.sendResponseHeaders(405, 0);
                                }

                            } else {
                                System.out.println("Указан некорректный идентификатор задачи " + taskTypeIdentifier + ".");
                                httpExchange.sendResponseHeaders(405, 0);
                            }

                            break;
                        }

                        if (Pattern.matches("^/tasks/task$", requestPath)) {
                            String response = gson.toJson(taskManager.getAllTasks());
                            System.out.println("Извлекаем из менеджера все задачи.");
                            sendText(httpExchange, response);
                            break;
                        } else if (Pattern.matches("^/tasks/subtask$", requestPath)) {
                            String response = gson.toJson(taskManager.getAllSubtasks());
                            System.out.println("Извлекаем из менеджера все подзадачи.");
                            sendText(httpExchange, response);
                            break;
                        } else if (Pattern.matches("^/tasks/epic$", requestPath)) {
                            String response = gson.toJson(taskManager.getAllEpics());
                            System.out.println("Извлекаем из менеджера все эпики.");
                            sendText(httpExchange, response);
                            break;
                        } else if (Pattern.matches("^/tasks/history$", requestPath)) {
                            String response = gson.toJson(taskManager.getHistory());
                            System.out.println("Извлекаем из менеджера историю задач.");
                            sendText(httpExchange, response);
                            break;
                        } else {
                            System.out.println("Указан некорректный идентификатор задачи " + taskTypeIdentifier + ".");
                            httpExchange.sendResponseHeaders(405, 0);
                        }

                        break;

                    case "DELETE":
                        if (requestQuery != null) {
                            String queryId = requestQuery.replaceFirst("id=", "");
                            int id = parseQueryId(queryId);

                            if (isAcceptedTaskType) {

                                if (id != -1) {
                                    taskManager.deleteAnyTask(id);
                                    System.out.println("Задача с id " + id + " была удалена из менеджера.");
                                    httpExchange.sendResponseHeaders(200, 0);
                                } else {
                                    System.out.println("Получен некорректный id " + id + ".");
                                    httpExchange.sendResponseHeaders(405, 0);
                                }

                            } else {
                                System.out.println("Указан некорректный идентификатор задачи " + taskTypeIdentifier + ".");
                                httpExchange.sendResponseHeaders(405, 0);
                            }

                            break;
                        }

                        if (Pattern.matches("^/tasks/task$", requestPath)
                                || Pattern.matches("^/tasks/subtask$", requestPath)
                                || Pattern.matches("^/tasks/epic$", requestPath)) {

                            taskManager.deleteAllTasks();

                            System.out.println("Все задачи были удалены из менеджера.\n");
                            httpExchange.sendResponseHeaders(200, 0);

                            break;
                        }

                        break;

                    default:
                        System.out.println("Такой запрос " + requestMethod + " не обрабатывается.");
                        httpExchange.sendResponseHeaders(405, 0);
                }
            }

            if (Pattern.matches("^/tasks$", requestPath) && requestMethod.equals("GET")) {
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                System.out.println("Извлекаем из менеджера список приоритетных задач.");
                sendText(httpExchange, response);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int parseQueryId(String queryId) {
        try {
            return Integer.parseInt(queryId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    protected void start() {
        System.out.println("Запущен сервер на порту " + PORT);
        System.out.println("http://localhost:" + PORT);
        server.start();
    }

    protected void stop() {
        System.out.println("Остановлен сервер на порту " + PORT);
        server.stop(0);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
