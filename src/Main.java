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
        //initializes all courses after reading from JSON
        for (Course c: ref.courses) {
            c.setCompleted(c.getCompleted());
            c.initializeSelections();
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
        int slot = update.get("slot").getAsInt();
        boolean completed = update.get("completed").getAsBoolean();
        String selected = update.get("selected").getAsString();

        if (index < 0 || index >= ref.courses.length) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
            return;
        }
        Course c = ref.courses[index];
        //Used for requirements that need multiple courses so it updates one selection rather than the whole requirement
        c.setSelectedCourse(slot, selected);
        c.setCourseCompleted(slot, completed);

        //recalculated credits because some requirements can have a variable amount of credits based on chosen classes
        int[] totals = calculateCredits(ref.courses);

        exchange.sendResponseHeaders(204, -1);
        exchange.close();

    });

    server.createContext("/credits", exchange -> {
        int[] totals = calculateCredits(ref.courses);

        Map<String, Integer> credits = new HashMap<>();
        credits.put("DS", totals[0]);
        credits.put("ISB", totals[1]);
        credits.put("core", totals[2]);

        //totals credits from ISB, DS, and Core but some classes can apply to both majors
        credits.put("total", totals[0] + totals[1] + totals[2]);


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
    int totalISB = 0;
    int totalCore = 0;
    for (Course c: courses) {
        int remaining = c.getRemainingCredits();
        if (c.getBlock().equals("Major in Data Science (Mathematics)")) {
            totalDS += remaining;
        }
        else if (c.getBlock().equals("Major in Integrated Sci \u0026 Business")) {
            totalISB += remaining;
        }
        else {
            totalCore += remaining;
        }
    }
    return new int[] {totalDS, totalISB, totalCore};
}
