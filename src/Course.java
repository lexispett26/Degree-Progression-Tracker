public class Course {
    private String block;
    private String label;
    private String type;
    private int credits;
    private String applied;
    private String[] allowed;
    private String selected;

    public Course(String block, String label, String type, int credits, String applied, String[] allowed) {
        this.block = block;
        this.label = label;
        this.type = type;
        this.credits = credits;
        this.applied = applied;
        this.allowed = allowed;
        selected = null;
    }

    public String getLabel() {
        return label;
    }
    public String getType() {
        return type;
    }

    public int getCredits() {
        return credits;
    }


    public boolean getCompleted() {
        return applied != null;
    }

    public String getApplied() {
        return applied;
    }

    public String[] getAllowed() {
        return allowed;
    }

    public void setSelected(String chosen) {
        selected = chosen;
    }

    public String getBlock() {
        return block;
    }
}



