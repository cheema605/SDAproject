package org.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// class to hold all data
public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Lab> labs = new ArrayList<>();
    private List<Instructor> instructors = new ArrayList<>();
    private List<TA> tas = new ArrayList<>();
    private List<MakeupLabRequest> requests = new ArrayList<>();

    // getters
    public List<Lab> getLabs() {
        return labs;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public List<TA> getTas() {
        return tas;
    }

    public List<MakeupLabRequest> getRequests() {
        return requests;
    }
}
