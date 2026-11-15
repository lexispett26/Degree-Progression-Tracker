public class Course {
    private String label;
    private int credits;
    private String[] applied;
    private String[] allowed;

    public Course(String label, int credits, String[] applied, String[] allowed) {
        this.label = label;
        this.credits = credits;
        this.applied = applied;
        this.allowed = allowed;
    }

    public String getLabel() {
        return label;
    }

    public int getCredits() {
        return credits;
    }


    public boolean getCompleted() {
        return applied != null && applied.length > 0;
    }

    public String[] getAllowed() {
        return allowed;
    }

}



