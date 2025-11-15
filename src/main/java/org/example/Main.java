package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {
    private final ObservableList<Lab> labs = FXCollections.observableArrayList();
    private final LabRepository repository = new LabRepository(Path.of("data", "datastore.dat"));
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private DataStore dataStore;
    private AcademicOfficer academicOfficer;
    
    // Color scheme
    private static final String PRIMARY_COLOR = "#1976D2";
    private static final String SECONDARY_COLOR = "#455A64";
    private static final String BACKGROUND_COLOR = "#FAFAFA";
    private static final String CARD_BACKGROUND = "#FFFFFF";

    @Override
    public void start(Stage stage) {
        // Initialize with sample data
        dataStore = SampleDataInitializer.generateSampleData();
        academicOfficer = new AcademicOfficer(dataStore);
        labs.setAll(dataStore.getLabs());

        // Create modern table view with styling
        TableView<Lab> table = new TableView<>(labs);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 11; -fx-table-cell-border-color: #E0E0E0;");
        
        // Table columns
        TableColumn<Lab, String> idCol = new TableColumn<>("Lab ID");
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue() == null || cell.getValue().getId() == null ? "" : cell.getValue().getId()));
        idCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<Lab, String> nameCol = new TableColumn<>("Lab Name");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue() == null || cell.getValue().getName() == null ? "" : cell.getValue().getName()));
        
        TableColumn<Lab, String> venueCol = new TableColumn<>("Location");
        venueCol.setPrefWidth(140);
        venueCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue() == null || cell.getValue().getVenue() == null ? "N/A" : cell.getValue().getVenue().toString()));
        
        TableColumn<Lab, String> instrCol = new TableColumn<>("Instructor");
        instrCol.setPrefWidth(120);
        instrCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue() == null || cell.getValue().getInstructor() == null ? "Unassigned" : cell.getValue().getInstructor().getName()));
        
        TableColumn<Lab, String> tasCol = new TableColumn<>("Teaching Assistants");
        tasCol.setPrefWidth(150);
        tasCol.setCellValueFactory(cell -> {
            if (cell.getValue() == null || cell.getValue().getTas().isEmpty()) return new SimpleStringProperty("None");
            StringBuilder sb = new StringBuilder();
            for (TA ta : cell.getValue().getTas()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(ta.getName());
            }
            return new SimpleStringProperty(sb.toString());
        });

        table.getColumns().add(idCol);
        table.getColumns().add(nameCol);
        table.getColumns().add(venueCol);
        table.getColumns().add(instrCol);
        table.getColumns().add(tasCol);
        
        // Create header with title
        VBox header = createHeader();
        
        // Create modern control buttons
        VBox controlPanel = createControlPanel();
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        root.setTop(header);
        root.setCenter(createTableContainer(table));
        root.setBottom(controlPanel);
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Labs Management System");
        stage.show();
    }
    
    private VBox createHeader() {
        Label title = new Label("Labs Management System");
        title.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        Label subtitle = new Label("University Lab & Scheduling Administration Platform");
        subtitle.setStyle("-fx-font-size: 12; -fx-text-fill: " + SECONDARY_COLOR + ";");
        
        VBox headerBox = new VBox(6, title, subtitle);
        headerBox.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-border-color: #E0E0E0; -fx-border-width: 0 0 2 0;");
        headerBox.setPadding(new Insets(20));
        headerBox.setAlignment(Pos.TOP_LEFT);
        
        return headerBox;
    }
    
    private VBox createTableContainer(TableView<Lab> table) {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-border-color: #E0E0E0; -fx-border-width: 1;");
        container.setPadding(new Insets(15));
        container.setSpacing(10);
        
        Label tableTitle = new Label("Active Labs");
        tableTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_COLOR + ";");
        
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        container.getChildren().addAll(tableTitle, table);
        
        return container;
    }
    
    private VBox createControlPanel() {
        // Primary action buttons (top row)
        Button loadBtn = createStyledButton("📂 Load", "#4CAF50");
        Button saveBtn = createStyledButton("💾 Save", "#2196F3");
        Button addBtn = createStyledButton("➕ Add Lab", PRIMARY_COLOR);
        
        HBox primaryActions = new HBox(10, loadBtn, saveBtn, addBtn);
        primaryActions.setPadding(new Insets(10));
        primaryActions.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-border-color: #E0E0E0; -fx-border-width: 1 0 0 0;");
        
        // Secondary action buttons (second row)
        Button assignBtn = createStyledButton("👥 Assign Staff", PRIMARY_COLOR);
        Button enterTSBtn = createStyledButton("⏱️ TimeSheet", "#FF9800");
        Button makeupBtn = createStyledButton("🔄 Makeup Lab", "#9C27B0");
        
        HBox secondaryActions = new HBox(10, assignBtn, enterTSBtn, makeupBtn);
        secondaryActions.setPadding(new Insets(10));
        secondaryActions.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
        
        // Report buttons (third row)
        Button scheduleReportBtn = createStyledButton("📋 Schedule Report", "#607D8B");
        Button timesheetReportBtn = createStyledButton("📊 Timesheet Report", "#607D8B");
        Button labReportBtn = createStyledButton("📈 Lab Report", "#607D8B");
        
        HBox reportActions = new HBox(10, scheduleReportBtn, timesheetReportBtn, labReportBtn);
        reportActions.setPadding(new Insets(10));
        reportActions.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-border-color: #E0E0E0; -fx-border-width: 1 0 0 0;");
        
        // Set button actions
        loadBtn.setOnAction(e -> loadData());
        saveBtn.setOnAction(e -> saveData());
        addBtn.setOnAction(e -> addLabDialog());
        assignBtn.setOnAction(e -> assignDialog());
        enterTSBtn.setOnAction(e -> enterTimeSheetDialog());
        makeupBtn.setOnAction(e -> requestMakeupDialog());
        scheduleReportBtn.setOnAction(e -> generateWeeklyScheduleReport());
        timesheetReportBtn.setOnAction(e -> generateWeeklyTimeSheetReport());
        labReportBtn.setOnAction(e -> generateLabTimeSheetReport());
        
        VBox controlBox = new VBox(primaryActions, secondaryActions, reportActions);
        controlBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        return controlBox;
    }
    
    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-font-size: 11; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 4; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + " -fx-opacity: 0.9;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(" -fx-opacity: 0.9;", "")));
        btn.setMinWidth(130);
        return btn;
    }
    
    private void loadData() {
        try {
            DataStore ds = repository.load();
            labs.setAll(ds.getLabs());
            dataStore = ds;
            showSuccess("Load Successful", "Loaded " + ds.getLabs().size() + " labs from disk");
        } catch (Exception ex) {
            showError("Load Failed", "Could not load data: " + ex.getMessage());
        }
    }
    
    private void saveData() {
        try {
            DataStore ds = new DataStore();
            ds.getLabs().addAll(labs);
            repository.save(ds);
            showSuccess("Save Successful", "Saved " + labs.size() + " labs to disk");
        } catch (IOException ex) {
            showError("Save Failed", "Could not save data: " + ex.getMessage());
        }
    }

    private void addLabDialog() {
        Dialog<Lab> dlg = new Dialog<>();
        dlg.setTitle("Create New Lab");
        dlg.getDialogPane().setPrefSize(450, 400);
        
        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        TextField idField = createStyledTextField("LAB-001");
        TextField nameField = createStyledTextField("Lab Name");
        TextField buildingField = createStyledTextField("Building Name");
        TextField roomField = createStyledTextField("Room Number");
        TextField instrField = createStyledTextField("Instructor Name");
        TextField scheduleStart = createStyledTextField("2024-11-18 10:00");
        TextField scheduleEnd = createStyledTextField("2024-11-18 12:00");

        VBox form = new VBox(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 4;");
        
        form.getChildren().addAll(
            createFormGroup("Lab ID", idField),
            createFormGroup("Lab Name", nameField),
            createFormGroup("Building", buildingField),
            createFormGroup("Room Number", roomField),
            createFormGroup("Instructor Name", instrField),
            createFormGroup("Schedule Start (yyyy-MM-dd HH:mm)", scheduleStart),
            createFormGroup("Schedule End (yyyy-MM-dd HH:mm)", scheduleEnd)
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
                    showError("Invalid Input", "Please check your input: " + ex.getMessage());
                    return null;
                }
            }
            return null;
        });

        dlg.showAndWait().ifPresent(lab -> {
            labs.add(lab);
            showSuccess("Lab Created", "Lab '" + lab.getName() + "' has been created successfully");
        });
    }
    
    private void assignDialog() {
        Lab sel = pickLab();
        if (sel == null) return;

        Dialog<String> dlg = new Dialog<>();
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
        
        TextField nameField = createStyledTextField("Name");
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(15));
        form.getChildren().addAll(
            createFormGroup("Role", typeBox),
            createFormGroup("Staff Name", nameField)
        );
        
        dlg.getDialogPane().setContent(form);
        dlg.setResultConverter(btn -> btn == ButtonType.OK ? nameField.getText().trim() : null);
        
        dlg.showAndWait().ifPresent(name -> {
            if (instrRB.isSelected()) {
                academicOfficer.assignInstructor(sel.getId(), name);
                showSuccess("Assigned", "Instructor assigned to " + sel.getId());
            } else {
                academicOfficer.assignTA(sel.getId(), name);
                showSuccess("Assigned", "TA assigned to " + sel.getId());
            }
            refreshTable();
        });
    }

    private Lab pickLab() {
        if (labs.isEmpty()) {
            showError("No Labs Available", "Please create a lab first");
            return null;
        }
        ChoiceDialog<Lab> dlg = new ChoiceDialog<>(labs.get(0), labs);
        dlg.setTitle("Select Lab");
        dlg.setHeaderText("Choose a lab to work with");
        return dlg.showAndWait().orElse(null);
    }

    private void refreshTable() {
        labs.clear();
        labs.addAll(dataStore.getLabs());
    }

    private void enterTimeSheetDialog() {
        Lab sel = pickLab();
        if (sel == null) return;
        
        Dialog<TimeSheet> dlg = new Dialog<>();
        dlg.setTitle("Record TimeSheet Entry");
        dlg.getDialogPane().setPrefSize(400, 250);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField startField = createStyledTextField("2024-11-18 10:00");
        TextField endField = createStyledTextField("2024-11-18 12:00");
        CheckBox absentCheckBox = new CheckBox("Mark as Absent (Leave)");
        
        startField.disableProperty().bind(absentCheckBox.selectedProperty());
        endField.disableProperty().bind(absentCheckBox.selectedProperty());
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(15));
        form.getChildren().addAll(
            new Label("Lab: " + sel.getName()),
            createFormGroup("Actual Start (yyyy-MM-dd HH:mm)", startField),
            createFormGroup("Actual End (yyyy-MM-dd HH:mm)", endField),
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
                    showError("Invalid DateTime", "Please use format: yyyy-MM-dd HH:mm");
                    return null;
                }
            }
            return null;
        });
        
        dlg.showAndWait().ifPresent(ts -> {
            sel.addSession(ts);
            showSuccess("TimeSheet Added", "Entry recorded for " + sel.getId());
        });
    }

    private void requestMakeupDialog() {
        Lab sel = pickLab();
        if (sel == null) return;
        
        Dialog<MakeupLabRequest> dlg = new Dialog<>();
        dlg.setTitle("Request Makeup Lab Session");
        dlg.getDialogPane().setPrefSize(400, 250);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField startField = createStyledTextField("2024-12-02 10:00");
        TextField endField = createStyledTextField("2024-12-02 12:00");
        
        VBox form = new VBox(15);
        form.setPadding(new Insets(15));
        form.getChildren().addAll(
            new Label("Lab: " + sel.getName()),
            createFormGroup("Makeup Session Start (yyyy-MM-dd HH:mm)", startField),
            createFormGroup("Makeup Session End (yyyy-MM-dd HH:mm)", endField)
        );
        
        dlg.getDialogPane().setContent(form);
        
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    LocalDateTime s = LocalDateTime.parse(startField.getText().trim(), dtf);
                    LocalDateTime e = LocalDateTime.parse(endField.getText().trim(), dtf);
                    return new MakeupLabRequest("R-" + System.currentTimeMillis(), sel.getId(), 
                        sel.getInstructor() == null ? "" : sel.getInstructor().getId(), 
                        new Schedule(s, e));
                } catch (Exception ex) {
                    showError("Invalid DateTime", "Please use format: yyyy-MM-dd HH:mm");
                    return null;
                }
            }
            return null;
        });
        
        dlg.showAndWait().ifPresent(req -> {
            try {
                DataStore ds = repository.load();
                ds.getRequests().add(req);
                boolean found = ds.getLabs().stream().anyMatch(l -> l.getId().equals(sel.getId()));
                if (!found) ds.getLabs().add(sel);
                repository.save(ds);
                showSuccess("Makeup Requested", "Request submitted for approval");
            } catch (Exception ex) {
                showError("Error", "Could not save request: " + ex.getMessage());
            }
        });
    }

    private void generateWeeklyScheduleReport() {
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
        area.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        area.setEditable(false);
        area.setWrapText(true);
        dlg.getDialogPane().setContent(area);
        dlg.showAndWait();
    }
    
    private void generateWeeklyTimeSheetReport() {
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
        area.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        area.setEditable(false);
        area.setWrapText(true);
        dlg.getDialogPane().setContent(area);
        dlg.showAndWait();
    }
    
    private void generateLabTimeSheetReport() {
        Lab sel = pickLab();
        if (sel == null) return;
        
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Lab TimeSheet Report - " + sel.getName());
        dlg.getDialogPane().setPrefSize(600, 400);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("LAB TIMESHEET REPORT - SEMESTER VIEW\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        sb.append("Lab: ").append(sel.getName()).append(" (").append(sel.getId()).append(")\n");
        sb.append("Instructor: ").append(sel.getInstructor() == null ? "Unassigned" : sel.getInstructor().getName()).append("\n");
        sb.append("Location: ").append(sel.getVenue() == null ? "Not Set" : sel.getVenue()).append("\n\n");
        
        sb.append("CONTACT HOURS: ").append(sel.totalContactHours()).append(" hours\n");
        sb.append("LEAVES/ABSENCES: ").append(sel.leavesCount()).append("\n");
        sb.append("TOTAL SESSIONS: ").append(sel.getSessions().size()).append("\n\n");
        
        sb.append("SESSION DETAILS:\n");
        for (int i = 0; i < sel.getSessions().size(); i++) {
            TimeSheet ts = sel.getSessions().get(i);
            sb.append(String.format("  %2d. ", i + 1));
            if (ts.getActualStart() == null || ts.getActualEnd() == null) {
                sb.append("[ABSENT]");
            } else {
                sb.append(ts.getActualStart()).append(" → ").append(ts.getActualEnd());
            }
            sb.append("\n");
        }
        
        TextArea area = new TextArea(sb.toString());
        area.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        area.setEditable(false);
        area.setWrapText(true);
        dlg.getDialogPane().setContent(area);
        dlg.showAndWait();
    }
    
    private HBox createFormGroup(String label, javafx.scene.Node control) {
        HBox group = new HBox(10);
        group.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + SECONDARY_COLOR + "; -fx-min-width: 200;");
        HBox.setHgrow(control, javafx.scene.layout.Priority.ALWAYS);
        group.getChildren().addAll(lbl, control);
        return group;
    }
    
    private TextField createStyledTextField(String promptText) {
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setStyle("-fx-padding: 8; -fx-font-size: 11; -fx-border-color: #BCC1C6; -fx-border-radius: 4; -fx-padding: 8;");
        tf.setMinHeight(32);
        return tf;
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-size: 11;");
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-size: 11;");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
