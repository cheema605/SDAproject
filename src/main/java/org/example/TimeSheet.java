package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeSheet implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;

    public TimeSheet() {}

    public TimeSheet(LocalDateTime actualStart, LocalDateTime actualEnd) {
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
    }

    public LocalDateTime getActualStart() {
        return actualStart;
    }

    public void setActualStart(LocalDateTime actualStart) {
        this.actualStart = actualStart;
    }

    public LocalDateTime getActualEnd() {
        return actualEnd;
    }

    public void setActualEnd(LocalDateTime actualEnd) {
        this.actualEnd = actualEnd;
    }

    @Override
    public String toString() {
        return actualStart + " to " + actualEnd;
    }
}
