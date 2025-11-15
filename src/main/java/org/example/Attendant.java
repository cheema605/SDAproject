package org.example;

import java.time.LocalDateTime;

public class Attendant {
    private String building;
    private DataStore dataStore;

    public Attendant(String building, DataStore dataStore) {
        this.building = building;
        this.dataStore = dataStore;
    }

    public void enterTimeSheet(String labId, LocalDateTime actualStart, LocalDateTime actualEnd) {
        Lab lab = findLab(labId);
        if (lab != null) {
            TimeSheet ts = new TimeSheet(actualStart, actualEnd);
            lab.addSession(ts);
        }
    }

    public void approveMakeupRequest(String requestId) {
        MakeupLabRequest req = dataStore.getRequests().stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElse(null);
        if (req != null) {
            req.setApproved(true);
        }
    }

    public String getBuilding() {
        return building;
    }

    private Lab findLab(String labId) {
        return dataStore.getLabs().stream()
                .filter(l -> l.getId().equals(labId))
                .findFirst()
                .orElse(null);
    }
}
