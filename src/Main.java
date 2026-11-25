import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;

public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    var ref = new Object() {
        Course[] courses = null;
    };

    try (Reader reader = new FileReader("docs/my_plan.json")) {
        Gson gson = new Gson();
        ref.courses = gson.fromJson(reader, Course[].class);
        for (Course c: ref.courses) {
            c.setCompleted(c.getCompleted());
            if (!c.getCompleted()) {
                c.setSelected(c.getApplied());
            }
        }
    }
    catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    catch (IOException e) {
        e.printStackTrace();
    }

    server.createContext("/", exchange -> {

        byte[] bytes = Files.readAllBytes(Path.of("dep/index.html"));
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    });

    server.createContext("/courses", exchange -> {
        byte[] bytes = new Gson().toJson(ref.courses).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }

    });

    server.createContext("/update-course", exchange -> {
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        JsonObject update = JsonParser.parseString(body).getAsJsonObject();

        int index = update.get("index").getAsInt();
        boolean completed = update.get("completed").getAsBoolean();

        System.out.println("Received: " + index + ", " + completed);

        if (index < 0 || index >= ref.courses.length) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
            return;
        }
        ref.courses[index].setCompleted(completed);

        int[] totals = calculateCredits(ref.courses);
        System.out.println("DS remaining: " + totals[0]);
        System.out.println("Core remaining: " + totals[1]);

        exchange.sendResponseHeaders(204, -1);
        exchange.close();

    });

    server.createContext("/credits", exchange -> {
        int[] totals = calculateCredits(ref.courses);

        Map<String, Integer> credits = new HashMap<>();
        credits.put("major", totals[0]);
        credits.put("core", totals[1]);
        credits.put("total", totals[0] + totals[1]);


        byte[] bytes = new Gson().toJson(credits)
                .getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=utf-8"
        );

        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }


    });

    server.start();
    System.out.println("Server running at http://localhost:8080");

}

public static int[] calculateCredits(Course[] courses) {
    int totalDS = 0;
    int totalCore = 0;
    for (Course c: courses) {
        if (!c.getCompleted()) {
            if (c.getBlock().equals("Major in Data Science (Mathematics)")) {
                totalDS += c.getCredits();
            } else {
                totalCore += c.getCredits();
            }
        }
    }
    return new int[] {totalDS, totalCore};
}
