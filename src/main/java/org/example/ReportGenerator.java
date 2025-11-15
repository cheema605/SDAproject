package org.example;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;

/**
 * ReportGenerator handles all report generation logic.
 * Single Responsibility: Generate various reports from lab data.
 */
public class ReportGenerator {
    
    private final ObservableList<Lab> labs;
    
    public ReportGenerator(ObservableList<Lab> labs) {
        this.labs = labs;
    }
    
    /**
     * Generates and displays the weekly schedule report.
     */
    public void generateWeeklyScheduleReport() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Weekly Schedule Report");
        dlg.getDialogPane().setPrefSize(600, 400);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("WEEKLY SCHEDULE REPORT\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        
        for (Lab l : labs) {
            sb.append("📚 ").append(l.getId()).append(" - ").append(l.getName()).append("\n");
            sb.append("   Location: ").append(l.getVenue() == null ? "(Not Set)" : l.getVenue()).append("\n");
            sb.append("   Instructor: ").append(l.getInstructor() == null ? "(Unassigned)" : l.getInstructor().getName()).append("\n");
            sb.append("   Schedule: ").append(l.getSchedule() == null ? "(Not Set)" : l.getSchedule()).append("\n");
            sb.append("   TAs: ");
            if (l.getTas().isEmpty()) {
                sb.append("(None assigned)");
            } else {
                for (int i = 0; i < l.getTas().size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(l.getTas().get(i).getName());
                }
            }
            sb.append("\n\n");
        }
        
        TextArea area = new TextArea(sb.toString());
        area.setStyle(StyleManager.REPORT_TEXT_STYLE);
        area.setEditable(false);
        area.setWrapText(true);
        dlg.getDialogPane().setContent(area);
        dlg.showAndWait();
    }
    
    /**
     * Generates and displays the weekly timesheet report.
     */
    public void generateWeeklyTimeSheetReport() {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Weekly TimeSheet Report");
        dlg.getDialogPane().setPrefSize(600, 400);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("WEEKLY TIMESHEET REPORT\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        
        for (Lab l : labs) {
            sb.append("📚 ").append(l.getId()).append(" - ").append(l.getName()).append("\n");
            if (l.getSessions().isEmpty()) {
                sb.append("   No timesheet entries recorded\n\n");
                continue;
            }
            for (int i = 0; i < l.getSessions().size(); i++) {
                TimeSheet ts = l.getSessions().get(i);
                sb.append("   Session ").append(i + 1).append(": ");
                if (ts.getActualStart() == null || ts.getActualEnd() == null) {
                    sb.append("ABSENT/LEAVE");
                } else {
                    sb.append(ts.getActualStart()).append(" to ").append(ts.getActualEnd());
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        
        TextArea area = new TextArea(sb.toString());
        area.setStyle(StyleManager.REPORT_TEXT_STYLE);
        area.setEditable(false);
        area.setWrapText(true);
        dlg.getDialogPane().setContent(area);
        dlg.showAndWait();
    }
    
    /**
     * Generates and displays the lab timesheet report for a specific lab.
     */
    public void generateLabTimeSheetReport(Lab lab) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Lab TimeSheet Report - " + lab.getName());
        dlg.getDialogPane().setPrefSize(600, 400);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("LAB TIMESHEET REPORT - SEMESTER VIEW\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        sb.append("Lab: ").append(lab.getName()).append(" (").append(lab.getId()).append(")\n");
        sb.append("Instructor: ").append(lab.getInstructor() == null ? "Unassigned" : lab.getInstructor().getName()).append("\n");
        sb.append("Location: ").append(lab.getVenue() == null ? "Not Set" : lab.getVenue()).append("\n\n");
        
        sb.append("CONTACT HOURS: ").append(lab.totalContactHours()).append(" hours\n");
        sb.append("LEAVES/ABSENCES: ").append(lab.leavesCount()).append("\n");
        sb.append("TOTAL SESSIONS: ").append(lab.getSessions().size()).append("\n\n");
        
        sb.append("SESSION DETAILS:\n");
        for (int i = 0; i < lab.getSessions().size(); i++) {
            TimeSheet ts = lab.getSessions().get(i);
            sb.append(String.format("  %2d. ", i + 1));
            if (ts.getActualStart() == null || ts.getActualEnd() == null) {
                sb.append("[ABSENT]");
            } else {
                sb.append(ts.getActualStart()).append(" → ").append(ts.getActualEnd());
            }
            sb.append("\n");
        }
        
        TextArea area = new TextArea(sb.toString());
        area.setStyle(StyleManager.REPORT_TEXT_STYLE);
        area.setEditable(false);
        area.setWrapText(true);
        dlg.getDialogPane().setContent(area);
        dlg.showAndWait();
    }
}
