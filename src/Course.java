import java.util.Map;

public class Course {
    private String block;
    private String label;
    private String type;
    private int credits;
    private String applied;
    private String[] allowed;
    private String selected;
    private Boolean completed;
    private int required;
    private Map<String, Integer> courseCredits;

    private String[] selectedCourses;
    private boolean[] completedCourses;

    public Course(String block, String label, String type, int credits, String applied, String[] allowed, int required) {
        this.block = block;
        this.label = label;
        this.type = type;
        this.credits = credits;
        this.applied = applied;
        this.allowed = allowed;
        selected = null;
        this.required = required;
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

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getCompleted() {
        if (completed != null) {
            return completed;
        }
        return applied != null;
    }

    public String[] getSelectedCourses() {
        return selectedCourses;
    }

    public boolean[] getCompletedCourses() {
        return completedCourses;
    }
    //defaults required courses to 1 when the required value isn't present for that course
    public int getRequiredCourses() {
        return required > 0 ? required : 1;
    }

    //some selectives have different credit values so finds credits based on specific courses for necessary selectives
    public int getCourseCredits(String courseCode) {
        if (courseCredits != null) {
            Integer value = courseCredits.get(courseCode);

            if (value == null) {
                throw new IllegalArgumentException(
                        "Missing credits for " + courseCode
                );
            }

            return value;
        }

        return credits;
    }

    public void initializeSelections() {
        int count = getRequiredCourses();

        if (selectedCourses == null || selectedCourses.length != count) {
            selectedCourses = new String[count];
        }

        if (completedCourses == null || completedCourses.length != count) {
            completedCourses = new boolean[count];
        }

        if (applied != null && !applied.isEmpty()) {
            for (String option : allowed) {
                if (option.equals(applied)) {
                    selectedCourses[0] = applied;
                    completedCourses[0] = getCompleted();
                    break;
                }
            }
        }
    }

    public void setSelectedCourse(int slot, String code) {
        if (slot < 0 || slot >= selectedCourses.length) {
            throw new IllegalArgumentException("Invalid course slot");
        }

        if (code != null && !code.isEmpty()) {
            boolean valid = false;

            for (String option : allowed) {
                if (option.equals(code)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                throw new IllegalArgumentException("Invalid course: " + code);
            }
        }

        if (code != null && code.isEmpty()) {
            code = null;
        }

        if (!java.util.Objects.equals(selectedCourses[slot], code)) {
            selectedCourses[slot] = code;
            completedCourses[slot] = false;
        }
    }

    public void setCourseCompleted(int slot, boolean completed) {
        if (slot < 0 || slot >= completedCourses.length) {
            throw new IllegalArgumentException("Invalid course slot");
        }

        if (completed && selectedCourses[slot] == null) {
            throw new IllegalArgumentException(
                    "Select a course before marking it complete"
            );
        }

        completedCourses[slot] = completed;
    }

    public int getRemainingCredits() {
        int remaining = 0;

        for (int i = 0; i < getRequiredCourses(); i++) {
            if (completedCourses[i]) {
                continue;
            }

            String selected = selectedCourses[i];

            if (selected != null) {
                remaining += getCourseCredits(selected);
            } else if (courseCredits != null) {
                // originally calculates minimum total credits until specific higher credit classes are chosen
                int minimum = Integer.MAX_VALUE;

                for (int value : courseCredits.values()) {
                    minimum = Math.min(minimum, value);
                }

                if (minimum == Integer.MAX_VALUE) {
                    throw new IllegalStateException("No course credits defined");
                }

                remaining += minimum;
            } else {
                remaining += credits;
            }
        }

        return remaining;
    }
}



