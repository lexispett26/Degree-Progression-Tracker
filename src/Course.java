public class Course {
    private String name;
    private int credits;
    private boolean completed;

    public Course(String name, int credits, boolean completed) {
        this.name = name;
        this.credits = credits;
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public boolean getCompleted() {
        return completed;
    }

}



