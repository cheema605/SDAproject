package org.example;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DialogFactory creates and manages all dialog windows.
 * Single Responsibility: Construct and configure dialog UI components.
 */
public class DialogFactory {
    
    private final ObservableList<Lab> labs;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public DialogFactory(ObservableList<Lab> labs) {
        this.labs = labs;
    }
    
    /**
     * Creates the "Add Lab" dialog.
     */
    public Dialog<Lab> createAddLabDialog() {
        Dialog<Lab> dlg = new Dialog<>();
        dlg.setTitle("Create New Lab");
        dlg.getDialogPane().setPrefSize(450, 400);
        
        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        TextField idField = StyleManager.createStyledTextField("LAB-001");
        TextField nameField = StyleManager.createStyledTextField("Lab Name");
        TextField buildingField = StyleManager.createStyledTextField("Building Name");
        TextField roomField = StyleManager.createStyledTextField("Room Number");
        TextField instrField = StyleManager.createStyledTextField("Instructor Name");
        TextField scheduleStart = StyleManager.createStyledTextField("2024-11-18 10:00");
        TextField scheduleEnd = StyleManager.createStyledTextField("2024-11-18 12:00");

        VBox form = new VBox(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: " + StyleManager.BORDER_COLOR + "; -fx-border-radius: 4;");
        
        form.getChildren().addAll(
            StyleManager.createFormGroup("Lab ID", idField),
            StyleManager.createFormGroup("Lab Name", nameField),
            StyleManager.createFormGroup("Building", buildingField),
            StyleManager.createFormGroup("Room Number", roomField),
            StyleManager.createFormGroup("Instructor Name", instrField),
            StyleManager.createFormGroup("Schedule Start (yyyy-MM-dd HH:mm)", scheduleStart),
            StyleManager.createFormGroup("Schedule End (yyyy-MM-dd HH:mm)", scheduleEnd)
        );
        
        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        dlg.getDialogPane().setContent(scroll);

        dlg.setResultConverter(btn -> {
            if (btn == createBtn) {
                try {
                    Lab lab = new Lab(idField.getText().trim(), nameField.getText().trim());
                    lab.setVenue(new Venue(buildingField.getText().trim(), roomField.getText().trim()));
                    lab.setInstructor(new Instructor("I-" + idField.getText().trim(), instrField.getText().trim()));
                    LocalDateTime s = LocalDateTime.parse(scheduleStart.getText().trim(), dtf);
                    LocalDateTime e = LocalDateTime.parse(scheduleEnd.getText().trim(), dtf);
                    lab.setSchedule(new Schedule(s, e));
                    return lab;
                } catch (Exception ex) {
                    AlertHelper.showError("Invalid Input", "Please check your input: " + ex.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        return dlg;
    }
    
    /**
     * Creates the "Assign Staff" dialog.
     */
    public Dialog<AssignStaffResult> createAssignStaffDialog(Lab lab) {
        Dialog<AssignStaffResult> dlg = new Dialog<>();
        dlg.setTitle("Assign Staff to Lab");
        dlg.getDialogPane().setPrefSize(400, 200);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        RadioButton instrRB = new RadioButton("Assign Instructor");
        RadioButton taRB = new RadioButton("Assign TA");
        ToggleGroup tg = new ToggleGroup();
        instrRB.setToggleGroup(tg);
        taRB.setToggleGroup(tg);
        instrRB.setSelected(true);

        HBox typeBox = new HBox(15, instrRB, taRB);
        typeBox.setPadding(new Insets(10));
        
        TextField nameField = StyleManager.createStyledTextField("Name");
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(15));
        form.getChildren().addAll(
            StyleManager.createFormGroup("Role", typeBox),
            StyleManager.createFormGroup("Staff Name", nameField)
        );
        
        dlg.getDialogPane().setContent(form);
        
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new AssignStaffResult(nameField.getText().trim(), instrRB.isSelected());
            }
            return null;
        });
        
        return dlg;
    }
    
    /**
     * Creates the "Enter TimeSheet" dialog.
     */
    public Dialog<TimeSheet> createEnterTimeSheetDialog(Lab lab) {
        Dialog<TimeSheet> dlg = new Dialog<>();
        dlg.setTitle("Record TimeSheet Entry");
        dlg.getDialogPane().setPrefSize(400, 250);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField startField = StyleManager.createStyledTextField("2024-11-18 10:00");
        TextField endField = StyleManager.createStyledTextField("2024-11-18 12:00");
        CheckBox absentCheckBox = new CheckBox("Mark as Absent (Leave)");
        
        startField.disableProperty().bind(absentCheckBox.selectedProperty());
        endField.disableProperty().bind(absentCheckBox.selectedProperty());
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(15));
        form.getChildren().addAll(
            new Label("Lab: " + lab.getName()),
            StyleManager.createFormGroup("Actual Start (yyyy-MM-dd HH:mm)", startField),
            StyleManager.createFormGroup("Actual End (yyyy-MM-dd HH:mm)", endField),
            absentCheckBox
        );
        
        dlg.getDialogPane().setContent(form);
        
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    TimeSheet ts = new TimeSheet();
                    if (!absentCheckBox.isSelected()) {
                        if (!startField.getText().isBlank()) {
                            ts.setActualStart(LocalDateTime.parse(startField.getText().trim(), dtf));
                        }
                        if (!endField.getText().isBlank()) {
                            ts.setActualEnd(LocalDateTime.parse(endField.getText().trim(), dtf));
                        }
                    }
                    return ts;
                } catch (Exception ex) {
                    AlertHelper.showError("Invalid DateTime", "Please use format: yyyy-MM-dd HH:mm");
                    return null;
                }
            }
            return null;
        });
        
        return dlg;
    }
    
    /**
     * Creates the "Request Makeup Lab" dialog.
     */
    public Dialog<MakeupLabRequest> createRequestMakeupDialog(Lab lab) {
        Dialog<MakeupLabRequest> dlg = new Dialog<>();
        dlg.setTitle("Request Makeup Lab Session");
        dlg.getDialogPane().setPrefSize(400, 250);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField startField = StyleManager.createStyledTextField("2024-12-02 10:00");
        TextField endField = StyleManager.createStyledTextField("2024-12-02 12:00");
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(15));
        form.getChildren().addAll(
            new Label("Lab: " + lab.getName()),
            StyleManager.createFormGroup("Makeup Session Start (yyyy-MM-dd HH:mm)", startField),
            StyleManager.createFormGroup("Makeup Session End (yyyy-MM-dd HH:mm)", endField)
        );
        
        dlg.getDialogPane().setContent(form);
        
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    LocalDateTime s = LocalDateTime.parse(startField.getText().trim(), dtf);
                    LocalDateTime e = LocalDateTime.parse(endField.getText().trim(), dtf);
                    return new MakeupLabRequest("R-" + System.currentTimeMillis(), lab.getId(), 
                        lab.getInstructor() == null ? "" : lab.getInstructor().getId(), 
                        new Schedule(s, e));
                } catch (Exception ex) {
                    AlertHelper.showError("Invalid DateTime", "Please use format: yyyy-MM-dd HH:mm");
                    return null;
                }
            }
            return null;
        });
        
        return dlg;
    }
    
    /**
     * Creates the "Select Lab" dialog.
     */
    public Lab createSelectLabDialog() {
        if (labs.isEmpty()) {
            AlertHelper.showError("No Labs Available", "Please create a lab first");
            return null;
        }
        ChoiceDialog<Lab> dlg = new ChoiceDialog<>(labs.get(0), labs);
        dlg.setTitle("Select Lab");
        dlg.setHeaderText("Choose a lab to work with");
        return dlg.showAndWait().orElse(null);
    }
    
    /**
     * Helper class to return multiple values from assign staff dialog.
     */
    public static class AssignStaffResult {
        public String name;
        public boolean isInstructor;
        
        public AssignStaffResult(String name, boolean isInstructor) {
            this.name = name;
            this.isInstructor = isInstructor;
        }
    }
}
