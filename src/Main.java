import java.io.IOException;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;

public static void main(String[] args) {
    try (Reader reader = new FileReader("docs/my_plan.json")) {
        Gson gson = new Gson();
        Course[] courses = gson.fromJson(reader, Course[].class);
        int totalDS = 0;
        int totalCore = 0;
        int totalHonors = 0;
        for (Course c: courses) {
            if (!c.getCompleted()) {
                if (c.getBlock().equals("Major in Data Science (Mathematics)")) {
                    totalDS += c.getCredits();
                } else if (c.getBlock().equals("Honors College Curriculum")) {
                    totalHonors = 24;
                } else {
                    totalCore += c.getCredits();
                }
                c.setSelected(c.getApplied());
                IO.println(c.getLabel());
                String courseLine = "";
                for (String s : c.getAllowed()) {
                    courseLine += s;
                    courseLine += (" or ");
                }
                int length = courseLine.length();
                courseLine = courseLine.substring(0, length - 4);
                IO.println(courseLine);
                IO.println();

            }
        }
        IO.println("Total DS credits left: " + totalDS);
        IO.println("Total Science Core credits left: " + totalCore);
        IO.println("Total honors credits left: " + totalHonors);


    }
    catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    catch (IOException e) {
        e.printStackTrace();
    }

}