package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MakeupLabRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id; // request id
    private String labId;
    private String requestedByInstructorId;
    private Schedule schedule;
    private boolean approved;
    private LocalDateTime requestedAt;

    public MakeupLabRequest() {}

    public MakeupLabRequest(String id, String labId, String requestedByInstructorId, Schedule schedule) {
        this.id = id;
        this.labId = labId;
        this.requestedByInstructorId = requestedByInstructorId;
        this.schedule = schedule;
        this.approved = false;
        this.requestedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getLabId() { return labId; }
    public String getRequestedByInstructorId() { return requestedByInstructorId; }
    public Schedule getSchedule() { return schedule; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
}
