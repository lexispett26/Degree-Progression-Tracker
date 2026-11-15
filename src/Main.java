import java.io.IOException;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;

public static void main(String[] args) {
    try (Reader reader = new FileReader("docs/dsma.json")) {
        Gson gson = new Gson();
        Course[] courses = gson.fromJson(reader, Course[].class);

    }
    catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    catch (IOException e) {
        e.printStackTrace();
    }

}