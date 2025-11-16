package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * UIController manages all UI control logic and event handling.
 * Single Responsibility: Handle user interactions and UI state.
 * Depends on: DialogFactory, ReportGenerator, StyleManager, AlertHelper, LabRepository
 */
public class UIController {
    
    private final ObservableList<Lab> labs = FXCollections.observableArrayList();
    private final LabRepository repository;
    private final DialogFactory dialogFactory;
    private final ReportGenerator reportGenerator;
    
    private DataStore dataStore;
    private AcademicOfficer academicOfficer;
    
    public UIController(LabRepository repository) {
        this.repository = repository;
        this.dialogFactory = new DialogFactory(labs);
        this.reportGenerator = new ReportGenerator(labs);
        try {
            DataStore ds = repository.load();
            if (ds.getLabs() == null || ds.getLabs().isEmpty()) {
                ds = SampleDataInitializer.generateSampleData();
                repository.save(ds);
            }
            this.dataStore = ds;
        } catch (Exception e) {
            this.dataStore = SampleDataInitializer.generateSampleData();
            try { repository.save(this.dataStore); } catch (IOException ignored) {}
        }
        this.academicOfficer = new AcademicOfficer(dataStore);
        this.labs.setAll(dataStore.getLabs());
    }

    public enum LabViewMode { ALL, ACTIVE_NOW, TODAY }

    /** New mode-based filtering API. */
    public ObservableList<Lab> getLabsForUser(User user, LabViewMode mode) {
        ObservableList<Lab> result = FXCollections.observableArrayList();
        LocalDateTime now = LocalDateTime.now();

        for (Lab lab : dataStore.getLabs()) {
            boolean includeByActive = true;
            if (mode == LabViewMode.ACTIVE_NOW) {
                includeByActive = false;
                Schedule s = lab.getSchedule();
                if (s != null && s.getExpectedStart() != null && s.getExpectedEnd() != null) {
                    if (!now.isBefore(s.getExpectedStart()) && !now.isAfter(s.getExpectedEnd())) includeByActive = true;
                }
                if (!includeByActive) {
                    for (TimeSheet ts : lab.getSessions()) {
                        if (ts.getActualStart() != null && ts.getActualEnd() != null) {
                            if (!now.isBefore(ts.getActualStart()) && !now.isAfter(ts.getActualEnd())) { includeByActive = true; break; }
                        }
                    }
                }
            } else if (mode == LabViewMode.TODAY) {
                includeByActive = false;
                java.time.LocalDate today = now.toLocalDate();
                Schedule s = lab.getSchedule();
                if (s != null && s.getExpectedStart() != null) {
                    if (s.getExpectedStart().toLocalDate().equals(today)) includeByActive = true;
                }
                if (!includeByActive) {
                    for (TimeSheet ts : lab.getSessions()) {
                        if (ts.getActualStart() != null) {
                            if (ts.getActualStart().toLocalDate().equals(today)) { includeByActive = true; break; }
                        }
                    }
                }
            } // else ALL -> includeByActive stays true

            if (!includeByActive) continue;

            // role-based scoping
            switch (user.getRole()) {
                case ACADEMIC_OFFICER:
                    result.add(lab);
                    break;
                case ATTENDANT:
                    if (lab.getVenue() != null && lab.getVenue().getBuilding() != null
                            && user.getBuilding() != null
                            && lab.getVenue().getBuilding().equalsIgnoreCase(user.getBuilding())) {
                        result.add(lab);
                    }
                    break;
                case HOD:
                    result.add(lab);
                    break;
                case INSTRUCTOR:
                    if (lab.getInstructor() != null && lab.getInstructor().getName() != null
                            && lab.getInstructor().getName().equalsIgnoreCase(user.getName())) {
                        result.add(lab);
                    }
                    break;
                case TA:
                    for (TA ta : lab.getTas()) {
                        if (ta.getName() != null && ta.getName().equalsIgnoreCase(user.getName())) {
                            result.add(lab);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }
    
    public TableView<Lab> createLabsTable() {
        return createLabsTable(null);
    }

    /**
     * Create a table tailored to the given user role. If user is null, full columns are shown.
     */
    public TableView<Lab> createLabsTable(User user) {
        TableView<Lab> table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setStyle(StyleManager.TABLE_STYLE);

        // Lab ID
        TableColumn<Lab, String> idCol = new TableColumn<>("Lab ID");
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue() == null || cell.getValue().getId() == null ? "" : cell.getValue().getId()));
        idCol.setStyle("-fx-alignment: CENTER;");

        // Lab name
        TableColumn<Lab, String> nameCol = new TableColumn<>("Lab Name");
        nameCol.setPrefWidth(140);
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue() == null || cell.getValue().getName() == null ? "" : cell.getValue().getName()));

        // Venue (building - room)
        TableColumn<Lab, String> venueCol = new TableColumn<>("Location");
        venueCol.setPrefWidth(160);
        venueCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue() == null || cell.getValue().getVenue() == null ? "N/A" : cell.getValue().getVenue().toString()));

        table.getColumns().addAll(idCol, nameCol, venueCol);

        // Role-specific columns
        if (user == null || user.getRole() == User.Role.ACADEMIC_OFFICER || user.getRole() == User.Role.HOD) {
            TableColumn<Lab, String> instrCol = new TableColumn<>("Instructor");
            instrCol.setPrefWidth(140);
            instrCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue() == null || cell.getValue().getInstructor() == null ? "Unassigned" : cell.getValue().getInstructor().getName()));

            TableColumn<Lab, String> tasCol = new TableColumn<>("Teaching Assistants");
            tasCol.setPrefWidth(180);
            tasCol.setCellValueFactory(cell -> {
                if (cell.getValue() == null || cell.getValue().getTas().isEmpty()) return new javafx.beans.property.SimpleStringProperty("None");
                StringBuilder sb = new StringBuilder();
                for (TA ta : cell.getValue().getTas()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(ta.getName());
                }
                return new javafx.beans.property.SimpleStringProperty(sb.toString());
            });

            table.getColumns().addAll(instrCol, tasCol);
        } else if (user.getRole() == User.Role.INSTRUCTOR) {
            // Instructor sees TAs for their own labs (labs are filtered to instructor), no instructor column
            TableColumn<Lab, String> tasCol = new TableColumn<>("Teaching Assistants");
            tasCol.setPrefWidth(160);
            tasCol.setCellValueFactory(cell -> {
                if (cell.getValue() == null || cell.getValue().getTas().isEmpty()) return new javafx.beans.property.SimpleStringProperty("None");
                StringBuilder sb = new StringBuilder();
                for (TA ta : cell.getValue().getTas()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(ta.getName());
                }
                return new javafx.beans.property.SimpleStringProperty(sb.toString());
            });
            table.getColumns().add(tasCol);
        }

        // For roles that shouldn't see personnel lists (TA, Attendant), keep minimal columns only
        return table;
    }
    
    /**
     * Creates the header section with title and subtitle.
     */
    public VBox createHeader() {
        Label title = new Label("Labs Management System");
        title.setStyle(StyleManager.HEADER_TITLE_STYLE);
        
        Label subtitle = new Label("University Lab & Scheduling Administration Platform");
        subtitle.setStyle(StyleManager.HEADER_SUBTITLE_STYLE);
        
        VBox headerBox = new VBox(6, title, subtitle);
        headerBox.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                          "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                          "-fx-border-width: 0 0 2 0;");
        headerBox.setPadding(new Insets(20));
        
        return headerBox;
    }
    
    /**
     * Creates the table container with styling.
     */
    public VBox createTableContainer(TableView<Lab> table) {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                          "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                          "-fx-border-width: 1;");
        container.setPadding(new Insets(15));
        container.setSpacing(10);
        
        Label tableTitle = new Label("Active Labs");
        tableTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + StyleManager.SECONDARY_COLOR + ";");
        
        VBox.setVgrow(table, Priority.ALWAYS);
        container.getChildren().addAll(tableTitle, table);
        
        return container;
    }
    
    /**
     * Creates the control panel with all action buttons.
     */
    public VBox createControlPanel() {
        // Primary action buttons
        Button loadBtn = StyleManager.createStyledButton("📂 Load", "#4CAF50");
        Button saveBtn = StyleManager.createStyledButton("💾 Save", "#2196F3");
        Button addBtn = StyleManager.createStyledButton("➕ Add Lab", StyleManager.PRIMARY_COLOR);
        
        loadBtn.setOnAction(e -> handleLoad());
        saveBtn.setOnAction(e -> handleSave());
        addBtn.setOnAction(e -> handleAddLab());
        
        HBox primaryActions = new HBox(10, loadBtn, saveBtn, addBtn);
        primaryActions.setPadding(new Insets(10));
        primaryActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                               "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                               "-fx-border-width: 1 0 0 0;");
        
        // Secondary action buttons
        Button assignBtn = StyleManager.createStyledButton("👥 Assign Staff", StyleManager.PRIMARY_COLOR);
        Button enterTSBtn = StyleManager.createStyledButton("⏱️ TimeSheet", "#FF9800");
        Button makeupBtn = StyleManager.createStyledButton("🔄 Makeup Lab", "#9C27B0");
        
        assignBtn.setOnAction(e -> handleAssignStaff());
        enterTSBtn.setOnAction(e -> handleEnterTimeSheet());
        makeupBtn.setOnAction(e -> handleRequestMakeup());
        
        HBox secondaryActions = new HBox(10, assignBtn, enterTSBtn, makeupBtn);
        secondaryActions.setPadding(new Insets(10));
        secondaryActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + ";");
        
        // Report buttons
        Button scheduleReportBtn = StyleManager.createStyledButton("📋 Schedule Report", "#607D8B");
        Button timesheetReportBtn = StyleManager.createStyledButton("📊 Timesheet Report", "#607D8B");
        Button labReportBtn = StyleManager.createStyledButton("📈 Lab Report", "#607D8B");
        
        scheduleReportBtn.setOnAction(e -> reportGenerator.generateWeeklyScheduleReport());
        timesheetReportBtn.setOnAction(e -> reportGenerator.generateWeeklyTimeSheetReport());
        labReportBtn.setOnAction(e -> handleLabReport());
        
        HBox reportActions = new HBox(10, scheduleReportBtn, timesheetReportBtn, labReportBtn);
        reportActions.setPadding(new Insets(10));
        reportActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                              "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                              "-fx-border-width: 1 0 0 0;");
        
        VBox controlBox = new VBox(primaryActions, secondaryActions, reportActions);
        controlBox.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");
        
        return controlBox;
    }
    
    // Event handlers
    
    private void handleLoad() {
        try {
            DataStore ds = repository.load();
            labs.setAll(ds.getLabs());
            dataStore = ds;
            AlertHelper.showSuccess("Load Successful", "Loaded " + ds.getLabs().size() + " labs from disk");
        } catch (Exception ex) {
            AlertHelper.showError("Load Failed", "Could not load data: " + ex.getMessage());
        }
    }
    
    private void handleSave() {
        try {
            DataStore ds = new DataStore();
            ds.getLabs().addAll(labs);
            repository.save(ds);
            AlertHelper.showSuccess("Save Successful", "Saved " + labs.size() + " labs to disk");
        } catch (IOException ex) {
            AlertHelper.showError("Save Failed", "Could not save data: " + ex.getMessage());
        }
    }
    
    public void handleAddLab() {
        dialogFactory.createAddLabDialog().showAndWait().ifPresent(lab -> {
            labs.add(lab);
            AlertHelper.showSuccess("Lab Created", "Lab '" + lab.getName() + "' has been created successfully");
        });
    }
    
    public void handleAssignStaff() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel == null) return;

        dialogFactory.createAssignStaffDialog(sel).showAndWait().ifPresent(result -> {
            if (result.isInstructor) {
                academicOfficer.assignInstructor(sel.getId(), result.name);
                AlertHelper.showSuccess("Assigned", "Instructor assigned to " + sel.getId());
            } else {
                academicOfficer.assignTA(sel.getId(), result.name);
                AlertHelper.showSuccess("Assigned", "TA assigned to " + sel.getId());
            }
            refreshTable();
        });
    }

    public void handleEnterTimeSheet() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel == null) return;
        
        dialogFactory.createEnterTimeSheetDialog(sel).showAndWait().ifPresent(ts -> {
            sel.addSession(ts);
            AlertHelper.showSuccess("TimeSheet Added", "Entry recorded for " + sel.getId());
        });
    }

    public void handleRequestMakeup() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel == null) return;
        
        dialogFactory.createRequestMakeupDialog(sel).showAndWait().ifPresent(req -> {
            try {
                DataStore ds = repository.load();
                ds.getRequests().add(req);
                boolean found = ds.getLabs().stream().anyMatch(l -> l.getId().equals(sel.getId()));
                if (!found) ds.getLabs().add(sel);
                repository.save(ds);
                AlertHelper.showSuccess("Makeup Requested", "Request submitted for approval");
            } catch (Exception ex) {
                AlertHelper.showError("Error", "Could not save request: " + ex.getMessage());
            }
        });
    }

    public void handleLabReport() {
        Lab sel = dialogFactory.createSelectLabDialog();
        if (sel != null) {
            reportGenerator.generateLabTimeSheetReport(sel);
        }
    }
    
    /**
     * Returns labs filtered for the given user. If activeOnly is true, only
     * labs that are currently active (by schedule or ongoing session) are returned.
     */
    public ObservableList<Lab> getLabsForUser(User user, boolean activeOnly) {
        ObservableList<Lab> result = FXCollections.observableArrayList();
        LocalDateTime now = LocalDateTime.now();

        for (Lab lab : dataStore.getLabs()) {
            // check active state
            boolean active = true;
            if (activeOnly) {
                active = false;
                Schedule s = lab.getSchedule();
                if (s != null && s.getExpectedStart() != null && s.getExpectedEnd() != null) {
                    if (!now.isBefore(s.getExpectedStart()) && !now.isAfter(s.getExpectedEnd())) active = true;
                }
                if (!active) {
                    for (TimeSheet ts : lab.getSessions()) {
                        if (ts.getActualStart() != null && ts.getActualEnd() != null) {
                            if (!now.isBefore(ts.getActualStart()) && !now.isAfter(ts.getActualEnd())) { active = true; break; }
                        }
                    }
                }
            }

            if (!active) continue;

            // role-based scoping
            switch (user.getRole()) {
                case ACADEMIC_OFFICER:
                    result.add(lab);
                    break;
                case ATTENDANT:
                    if (lab.getVenue() != null && lab.getVenue().getBuilding() != null
                            && user.getBuilding() != null
                            && lab.getVenue().getBuilding().equalsIgnoreCase(user.getBuilding())) {
                        result.add(lab);
                    }
                    break;
                case HOD:
                    // HOD sees all labs for now
                    result.add(lab);
                    break;
                case INSTRUCTOR:
                    if (lab.getInstructor() != null && lab.getInstructor().getName() != null
                            && lab.getInstructor().getName().equalsIgnoreCase(user.getName())) {
                        result.add(lab);
                    }
                    break;
                case TA:
                    for (TA ta : lab.getTas()) {
                        if (ta.getName() != null && ta.getName().equalsIgnoreCase(user.getName())) {
                            result.add(lab);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    private void refreshTable() {
        labs.clear();
        labs.addAll(dataStore.getLabs());
    }
    
    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }
    
    public ObservableList<Lab> getLabs() {
        return labs;
    }
}
