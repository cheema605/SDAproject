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
 * InstructorView - Dashboard for Lab Instructor role.
 * Allows: Viewing labs, requesting makeup sessions.
 */
public class InstructorView {
    
    private final Stage stage;
    private final User user;
    private final LabRepository labRepository;
    private final UIController uiController;
    
    public InstructorView(Stage stage, User user) {
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
        root.setCenter(uiController.createTableContainer(uiController.createLabsTable()));
        
        // Controls - only Instructor features
        VBox controls = createControlPanel();
        root.setBottom(controls);
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Labs Management System - Lab Instructor");
        stage.show();
    }
    
    private VBox createHeader() {
        Label title = new Label("Labs Management System");
        title.setStyle(StyleManager.HEADER_TITLE_STYLE);
        
        Label userInfo = new Label("Logged in as: " + user.getName() + " (Lab Instructor)");
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
    
    private VBox createControlPanel() {
        Button viewLabsBtn = StyleManager.createStyledButton("📚 View My Labs", StyleManager.PRIMARY_COLOR);
        Button requestMakeupBtn = StyleManager.createStyledButton("🔄 Request Makeup Lab", "#FF9800");
        Button viewScheduleBtn = StyleManager.createStyledButton("📅 View Schedule", "#2196F3");
        
        viewLabsBtn.setOnAction(e -> AlertHelper.showInfo("My Labs", "View your assigned labs"));
        requestMakeupBtn.setOnAction(e -> uiController.handleRequestMakeup());
        viewScheduleBtn.setOnAction(e -> AlertHelper.showInfo("Schedule", "View your lab schedules"));
        
        HBox primaryActions = new HBox(10, viewLabsBtn, requestMakeupBtn, viewScheduleBtn);
        primaryActions.setPadding(new Insets(10));
        primaryActions.setStyle("-fx-background-color: " + StyleManager.CARD_BACKGROUND + "; " +
                               "-fx-border-color: " + StyleManager.BORDER_COLOR + "; " +
                               "-fx-border-width: 1 0 0 0;");
        
        VBox controlBox = new VBox(primaryActions);
        controlBox.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");
        
        return controlBox;
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
