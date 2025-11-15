package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.nio.file.Path;

/**
 * Main - Application Entry Point
 * 
 * Single Responsibility: Bootstrap the JavaFX application and orchestrate the main scene.
 * Follows SOLID principles by delegating all responsibilities to specialized classes:
 * 
 * - UIController: Manages all UI logic and event handling
 * - DialogFactory: Creates and configures all dialog windows
 * - ReportGenerator: Generates reports from lab data
 * - StyleManager: Manages all UI styling and visual constants
 * - AlertHelper: Handles all alert dialogs
 * - LabRepository: Manages data persistence
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) {
        // Initialize dependencies
        LabRepository repository = new LabRepository(Path.of("data", "datastore.dat"));
        UIController controller = new UIController(repository);
        
        // Build main UI
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + StyleManager.BACKGROUND_COLOR + ";");
        root.setTop(controller.createHeader());
        root.setCenter(controller.createTableContainer(controller.createLabsTable()));
        root.setBottom(controller.createControlPanel());
        
        // Configure and show stage
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Labs Management System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
