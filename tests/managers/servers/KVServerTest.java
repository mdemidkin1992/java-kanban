package managers.servers;

import com.google.gson.Gson;
import managers.utilities.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Maxim Demidkin
 * @Date 01.03.2023
 */

class KVServerTest {
    private KVServer kvServer;
    private final Gson gson = Managers.getGson();
    private String apiToken;
    private HttpClient httpClient;


    @BeforeEach
    public void init() throws IOException, InterruptedException {
        kvServer = new KVServer(8079);
        kvServer.start();
        httpClient = HttpClient.newHttpClient();
        apiToken = httpClient.send(HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8079/register"))
                        .GET()
                        .build()
                , HttpResponse.BodyHandlers.ofString()).body();
    }

    @AfterEach
    public void tearDown() {
        kvServer.stop();
    }

    @Test
    public void shouldReturnValueWhenKeyIsCorrect() throws IOException, InterruptedException {
        final String key = "10";
        final String value = "100";

        URI uri_1 = URI.create("http://localhost:8079/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(gson.toJson(value));
        HttpRequest request_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();

        assertEquals(200, httpClient.send(request_1, HttpResponse.BodyHandlers.ofString()).statusCode());

        URI uri_2 = URI.create("http://localhost:8079/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request_2 = HttpRequest.newBuilder().uri(uri_2).GET().build();

        assertEquals(gson.toJson(value), httpClient.send(request_2, HttpResponse.BodyHandlers.ofString()).body());
    }

    @Test
    public void shouldReturn403StatusCodeWhenApiTokenIsIncorrect() throws IOException, InterruptedException {
        String incorrectApiToken = "9879871236470123";
        final String key = "10";
        final String value = "100";

        URI uri_1 = URI.create("http://localhost:8079/save/" + key + "?API_TOKEN=" + incorrectApiToken);
        HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(gson.toJson(value));
        HttpRequest request_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();

        assertEquals(403, httpClient.send(request_1, HttpResponse.BodyHandlers.ofString()).statusCode());

        URI uri_2 = URI.create("http://localhost:8079/load/" + key + "?API_TOKEN=" + incorrectApiToken);
        HttpRequest request_2 = HttpRequest.newBuilder().uri(uri_2).GET().build();

        assertEquals(403, httpClient.send(request_2, HttpResponse.BodyHandlers.ofString()).statusCode());
    }

    @Test
    public void shouldReturn403StatusCodeWhenKeyIsEmpty() throws IOException, InterruptedException {
        final String key = "";
        final String value = "100";

        URI uri_1 = URI.create("http://localhost:8079/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(gson.toJson(value));
        HttpRequest request_1 = HttpRequest.newBuilder().uri(uri_1).POST(body_1).build();

        assertEquals(400, httpClient.send(request_1, HttpResponse.BodyHandlers.ofString()).statusCode());

        URI uri_2 = URI.create("http://localhost:8079/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request_2 = HttpRequest.newBuilder().uri(uri_2).GET().build();

        assertEquals(400, httpClient.send(request_2, HttpResponse.BodyHandlers.ofString()).statusCode());
    }

    @Test
    public void shouldReturn405StatusCodeWhenRequestMethodIncorrect() throws IOException, InterruptedException {
        final String key = "10";
        final String value = "100";

        URI uri_1 = URI.create("http://localhost:8079/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher body_1 = HttpRequest.BodyPublishers.ofString(gson.toJson(value));
        HttpRequest request_1 = HttpRequest.newBuilder().uri(uri_1).PUT(body_1).build();

        assertEquals(405, httpClient.send(request_1, HttpResponse.BodyHandlers.ofString()).statusCode());

        URI uri_2 = URI.create("http://localhost:8079/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request_2 = HttpRequest.newBuilder().uri(uri_2).DELETE().build();

        assertEquals(405, httpClient.send(request_2, HttpResponse.BodyHandlers.ofString()).statusCode());
    }

    @Test
    public void shouldReturn400StatusCodeWhenValueIsEmpty() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request_1 = HttpRequest.newBuilder().uri(URI.create("http://localhost:8079/register")).DELETE().build();
        assertEquals(405, httpClient.send(request_1, HttpResponse.BodyHandlers.ofString()).statusCode());
    }
}