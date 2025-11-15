package org.example;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HeadOfDepartment {
    private DataStore dataStore;

    public HeadOfDepartment(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public String generateWeeklyScheduleReport(int year, int week) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== WEEKLY SCHEDULE REPORT (Year ").append(year).append(", Week ").append(week).append(") ===\n\n");
        for (Lab lab : dataStore.getLabs()) {
            if (lab.getSchedule() != null) {
                LocalDateTime start = lab.getSchedule().getExpectedStart();
                if (start != null && start.getYear() == year && start.get(WeekFields.of(Locale.US).weekOfYear()) == week) {
                    sb.append("Lab ID: ").append(lab.getId()).append("\n");
                    sb.append("  Name: ").append(lab.getName()).append("\n");
                    sb.append("  Venue: ").append(lab.getVenue() == null ? "N/A" : lab.getVenue()).append("\n");
                    sb.append("  Instructor: ").append(lab.getInstructor() == null ? "Unassigned" : lab.getInstructor().getName()).append("\n");
                    sb.append("  Schedule: ").append(lab.getSchedule().getExpectedStart()).append(" to ").append(lab.getSchedule().getExpectedEnd()).append("\n\n");
                }
            }
        }
        return sb.toString();
    }

    public String generateWeeklyTimeSheetReport(int year, int week) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== WEEKLY TIMESHEET REPORT (Year ").append(year).append(", Week ").append(week).append(") ===\n\n");
        for (Lab lab : dataStore.getLabs()) {
            List<TimeSheet> weekSessions = new ArrayList<>();
            for (TimeSheet ts : lab.getSessions()) {
                if (ts.getActualStart() != null && ts.getActualStart().getYear() == year && 
                    ts.getActualStart().get(WeekFields.of(Locale.US).weekOfYear()) == week) {
                    weekSessions.add(ts);
                }
            }
            if (!weekSessions.isEmpty()) {
                sb.append("Lab ID: ").append(lab.getId()).append(" - ").append(lab.getName()).append("\n");
                for (TimeSheet ts : weekSessions) {
                    sb.append("  Session: ").append(ts.getActualStart()).append(" to ").append(ts.getActualEnd()).append("\n");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String generateLabTimeSheetReport(String labId) {
        Lab lab = dataStore.getLabs().stream()
                .filter(l -> l.getId().equals(labId))
                .findFirst()
                .orElse(null);
        if (lab == null) return "Lab not found: " + labId;

        StringBuilder sb = new StringBuilder();
        sb.append("=== SEMESTER TIMESHEET REPORT FOR LAB: ").append(lab.getId()).append(" (").append(lab.getName()).append(") ===\n\n");
        sb.append("Instructor: ").append(lab.getInstructor() == null ? "Unassigned" : lab.getInstructor().getName()).append("\n");
        sb.append("TAs: ");
        for (TA ta : lab.getTas()) {
            sb.append(ta.getName()).append(" ");
        }
        sb.append("\n\n");

        sb.append("Sessions:\n");
        for (TimeSheet ts : lab.getSessions()) {
            if (ts.getActualStart() != null && ts.getActualEnd() != null) {
                sb.append("  ").append(ts.getActualStart()).append(" to ").append(ts.getActualEnd()).append("\n");
            } else {
                sb.append("  ABSENT (leave recorded)\n");
            }
        }
        sb.append("\n");
        sb.append("Total Contact Hours: ").append(String.format("%.2f", lab.totalContactHours())).append("\n");
        sb.append("Leaves (absences): ").append(lab.leavesCount()).append("\n");
        sb.append("Total Sessions: ").append(lab.getSessions().size()).append("\n");

        // Include makeup request info
        List<MakeupLabRequest> makeups = dataStore.getRequests().stream()
                .filter(r -> r.getLabId().equals(labId) && r.isApproved())
                .toList();
        if (!makeups.isEmpty()) {
            sb.append("\nApproved Makeup Sessions:\n");
            for (MakeupLabRequest req : makeups) {
                sb.append("  ").append(req.getSchedule().getExpectedStart()).append(" to ").append(req.getSchedule().getExpectedEnd()).append("\n");
            }
        }

        return sb.toString();
    }
}
