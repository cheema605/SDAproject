package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;

// Schedule class
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDateTime expectedStart;
    private LocalDateTime expectedEnd;

    // constructor
    public Schedule() {
    }

    public Schedule(LocalDateTime expectedStart, LocalDateTime expectedEnd) {
        this.expectedStart = expectedStart;
        this.expectedEnd = expectedEnd;
    }

    // getters and setters
    public LocalDateTime getExpectedStart() {
        return expectedStart;
    }

    public void setExpectedStart(LocalDateTime expectedStart) {
        this.expectedStart = expectedStart;
    }

    public LocalDateTime getExpectedEnd() {
        return expectedEnd;
    }

    public void setExpectedEnd(LocalDateTime expectedEnd) {
        this.expectedEnd = expectedEnd;
    }

    @Override
    public String toString() {
        return expectedStart + " to " + expectedEnd;
    }
}
