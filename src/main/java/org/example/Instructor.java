package org.example;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends Person {
    private static final long serialVersionUID = 1L;
    private List<String> labIds = new ArrayList<>();

    public Instructor() {}

    public Instructor(String id, String name) {
        super(id, name);
    }

    public List<String> getLabIds() {
        return labIds;
    }

    public void assignLab(String labId) {
        if (!labIds.contains(labId)) labIds.add(labId);
    }
}
