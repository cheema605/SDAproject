package org.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Lab class
public class Lab implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private Venue venue;
    private Instructor instructor;
    private List<TA> tas = new ArrayList<>();
    private Schedule schedule;
    private java.util.List<TimeSheet> sessions = new java.util.ArrayList<>();

    // constructor
    public Lab() {
    }

    public Lab(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public List<TA> getTas() {
        return tas;
    }

    public void addTA(TA ta) {
        if (!tas.contains(ta))
            tas.add(ta);
    }

    public void removeTA(TA ta) {
        tas.remove(ta);
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public java.util.List<TimeSheet> getSessions() {
        return sessions;
    }

    public void addSession(TimeSheet ts) {
        if (ts != null)
            sessions.add(ts);
    }

    // calculate total hours
    public double totalContactHours() {
        double total = 0.0;
        for (TimeSheet t : sessions) {
            if (t.getActualStart() != null && t.getActualEnd() != null) {
                total += java.time.Duration.between(t.getActualStart(), t.getActualEnd()).toMinutes() / 60.0;
            }
        }
        return total;
    }

    // count leaves
    public long leavesCount() {
        long cnt = 0;
        for (TimeSheet t : sessions) {
            if (t.getActualStart() == null || t.getActualEnd() == null)
                cnt++;
        }
        return cnt;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
