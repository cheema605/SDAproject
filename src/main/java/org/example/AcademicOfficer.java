package org.example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AcademicOfficer {
    private DataStore dataStore;

    public AcademicOfficer(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Lab createLab(String id, String name, String building, String room, LocalDateTime schedStart, LocalDateTime schedEnd) {
        Lab lab = new Lab(id, name);
        lab.setVenue(new Venue(building, room));
        lab.setSchedule(new Schedule(schedStart, schedEnd));
        dataStore.getLabs().add(lab);
        return lab;
    }

    public void assignInstructor(String labId, String instructorName) {
        Lab lab = findLab(labId);
        if (lab != null) {
            Instructor instr = new Instructor("I-" + labId, instructorName);
            lab.setInstructor(instr);
            if (!dataStore.getInstructors().contains(instr)) {
                dataStore.getInstructors().add(instr);
            }
            instr.assignLab(labId);
        }
    }

    public void assignTA(String labId, String taName) {
        Lab lab = findLab(labId);
        if (lab != null) {
            TA ta = new TA("TA-" + labId + "-" + System.nanoTime(), taName);
            lab.addTA(ta);
            if (!dataStore.getTas().contains(ta)) {
                dataStore.getTas().add(ta);
            }
            ta.assignLab(labId);
        }
    }

    public void setSchedule(String labId, LocalDateTime start, LocalDateTime end) {
        Lab lab = findLab(labId);
        if (lab != null) {
            lab.setSchedule(new Schedule(start, end));
        }
    }

    private Lab findLab(String labId) {
        return dataStore.getLabs().stream()
                .filter(l -> l.getId().equals(labId))
                .findFirst()
                .orElse(null);
    }

    public DataStore getDataStore() {
        return dataStore;
    }
}
