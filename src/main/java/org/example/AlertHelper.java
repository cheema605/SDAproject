package org.example;

import javafx.scene.control.Alert;

/**
 * AlertHelper provides utility methods for displaying alerts.
 * Single Responsibility: Manage all alert dialogs with consistent styling.
 */
public class AlertHelper {
    
    /**
     * Displays a success/information alert.
     */
    public static void showSuccess(String title, String message) {
        showInfo(title, message);
    }
    
    /**
     * Displays an information alert.
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-size: 11;");
        alert.showAndWait();
    }

    /**
     * Displays an error alert.
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-size: 11;");
        alert.showAndWait();
    }

    /**
     * Displays a warning alert.
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-font-size: 11;");
        alert.showAndWait();
    }
}
