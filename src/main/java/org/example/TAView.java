package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.file.Path;

/**
 * TAView - Dashboard for Teaching Assistant role.
 * Allows: Viewing assigned labs, checking timesheets.
 */
public class TAView {
    
    private final Stage stage;
    private final User user;
    private final LabRepository labRepository;
    private final UIController uiController;
    
    public TAView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.labRepository = new LabRepository(Path.of("data", "datastore.dat"));
        this.uiController = new UIController(labRepository);
    }
    
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");
        
        // Header with user info
        VBox header = createHeader();
        root.setTop(header);
        
        // Main content - labs table
        TableView<Lab> table = uiController.createLabsTable(user);
        table.setItems(uiController.getLabsForUser(user, UIController.LabViewMode.ACTIVE_NOW));
        root.setCenter(uiController.createTableContainer(table));
        
        // Controls - only TA features
        VBox controls = createControlPanel(table);
        root.setBottom(controls);
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Labs Management System - Teaching Assistant");
        stage.show();
    }
    
    private VBox createHeader() {
        Label title = new Label("Labs Management System");
        title.setStyle(StyleManager.HEADER_TITLE_STYLE);
        
        Label userInfo = new Label("Logged in as: " + user.getName() + " (Teaching Assistant)");
        userInfo.setStyle("-fx-font-size: 12; -fx-text-fill: " + StyleManager.SECONDARY_COLOR + ";");
        
        Button logoutBtn = StyleManager.createStyledButton("Logout", "#F44336");
        logoutBtn.setPrefWidth(100);
        logoutBtn.setOnAction(e -> logout());
        
        HBox topRight = new HBox(10);
        topRight.setPadding(new Insets(10, 20, 10, 0));
        topRight.getChildren().add(logoutBtn);
        
        VBox headerContent = new VBox(6, title, userInfo);
        headerContent.setPadding(new Insets(20));
        headerContent.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                              "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                              "-fx-border-width: 0 0 2 0;");
        
        BorderPane headerPane = new BorderPane();
        headerPane.setLeft(headerContent);
        headerPane.setRight(topRight);
        
        return new VBox(headerPane);
    }
    
    private VBox createControlPanel(TableView<Lab> table) {
        Button viewAssignedBtn = StyleManager.createStyledButton("📚 View Assigned Labs", StyleManager.PRIMARY_COLOR);
        Button viewTimesheetBtn = StyleManager.createStyledButton("⏱️ View TimeSheet", "#FF9800");
        Button contactHoursBtn = StyleManager.createStyledButton("📊 Contact Hours", "#2196F3");

        viewAssignedBtn.setOnAction(e -> openAssignedLabsDialog());
        viewTimesheetBtn.setOnAction(e -> openTimesheetDialog());
        contactHoursBtn.setOnAction(e -> openContactHoursDialog());

        ChoiceBox<UIController.LabViewMode> viewChoice = new ChoiceBox<>();
        viewChoice.getItems().addAll(UIController.LabViewMode.ACTIVE_NOW, UIController.LabViewMode.ALL, UIController.LabViewMode.TODAY);
        viewChoice.setValue(UIController.LabViewMode.ACTIVE_NOW);
        viewChoice.setOnAction(e -> table.setItems(uiController.getLabsForUser(user, viewChoice.getValue())));

        HBox primaryActions = new HBox(10, viewAssignedBtn, viewTimesheetBtn, contactHoursBtn, new Label(" "), viewChoice);
        primaryActions.setPadding(new Insets(10));
        primaryActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                               "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                               "-fx-border-width: 1 0 0 0;");

        VBox controlBox = new VBox(primaryActions);
        controlBox.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");

        return controlBox;
    }
    
    private void openAssignedLabsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Assigned Labs");
        dialog.setHeaderText("Labs you are assigned to assist");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        ListView<String> labsList = new ListView<>();
        
        // Get all labs and filter by this TA
        uiController.getLabs().forEach(lab -> {
            for (TA ta : lab.getTas()) {
                if (ta.getName().equalsIgnoreCase(user.getName())) {
                    String timeStr = lab.getSchedule() != null ? 
                        lab.getSchedule().getExpectedStart().getHour() + ":00 - " + 
                        lab.getSchedule().getExpectedEnd().getHour() + ":00" : "TBD";
                    String instructorStr = lab.getInstructor() != null ? lab.getInstructor().getName() : "TBD";
                    labsList.getItems().add(lab.getName() + " - " + timeStr + " (Instructor: " + instructorStr + ")");
                    break;
                }
            }
        });
        
        if (labsList.getItems().isEmpty()) {
            labsList.getItems().add("No labs assigned");
        }
        
        labsList.setPrefHeight(200);
        
        content.getChildren().addAll(
            new Label("Your assigned labs:"),
            labsList
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private void openTimesheetDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("My TimeSheet");
        dialog.setHeaderText("Your recorded work hours");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        TextArea timesheetText = new TextArea();
        timesheetText.setText("Week 1: 10 hours\nWeek 2: 12 hours\nWeek 3: 11 hours\nTotal: 33 hours");
        timesheetText.setEditable(false);
        timesheetText.setWrapText(true);
        timesheetText.setPrefHeight(200);
        
        content.getChildren().addAll(
            new Label("Time Sheet Summary:"),
            timesheetText
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private void openContactHoursDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Contact Hours");
        dialog.setHeaderText("Your contact hours summary");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        TextArea hoursText = new TextArea();
        hoursText.setText("Total Contact Hours: 33 hours\n" +
                         "CS101: 11 hours\n" +
                         "CS102: 12 hours\n" +
                         "CS103: 10 hours\n" +
                         "Required: 40 hours\n" +
                         "Progress: 82.5%");
        hoursText.setEditable(false);
        hoursText.setWrapText(true);
        hoursText.setPrefHeight(220);
        
        content.getChildren().addAll(
            new Label("Contact Hours Tracking:"),
            hoursText
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    
    private void logout() {
        try {
            LoginUI loginUI = new LoginUI(new UserRepository(Path.of("data", "users.dat")));
            loginUI.show(stage);
        } catch (Exception ex) {
            AlertHelper.showError("Error", "Failed to load login screen");
        }
    }
}
