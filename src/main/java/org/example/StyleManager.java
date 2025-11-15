package org.example;

import javafx.scene.control.Button;

/**
 * StyleManager encapsulates all UI styling constants and methods.
 * Single Responsibility: Handle all visual styling concerns.
 */
public class StyleManager {
    
    // Color scheme - Material Design inspired
    public static final String PRIMARY_COLOR = "#1976D2";
    public static final String SECONDARY_COLOR = "#455A64";
    public static final String SUCCESS_COLOR = "#4CAF50";
    public static final String WARNING_COLOR = "#FF9800";
    public static final String DANGER_COLOR = "#F44336";
    public static final String BACKGROUND_COLOR = "#FAFAFA";
    public static final String CARD_BACKGROUND = "#FFFFFF";
    public static final String BORDER_COLOR = "#E0E0E0";
    
    // Component styles
    public static final String TABLE_STYLE = "-fx-font-size: 11; -fx-table-cell-border-color: " + BORDER_COLOR + ";";
    public static final String HEADER_TITLE_STYLE = "-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";";
    public static final String HEADER_SUBTITLE_STYLE = "-fx-font-size: 12; -fx-text-fill: " + SECONDARY_COLOR + ";";
    public static final String LABEL_STYLE = "-fx-font-weight: bold; -fx-text-fill: " + SECONDARY_COLOR + ";";
    public static final String REPORT_TEXT_STYLE = "-fx-font-family: 'Courier New'; -fx-font-size: 11;";
    
    /**
     * Creates a styled button with the given text and color.
     * Includes hover effects for better UX.
     */
    public static Button createStyledButton(String text, String color) {
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
    
    /**
     * Creates a styled text input field.
     */
    public static javafx.scene.control.TextField createStyledTextField(String promptText) {
        javafx.scene.control.TextField tf = new javafx.scene.control.TextField();
        tf.setPromptText(promptText);
        tf.setStyle("-fx-padding: 8; -fx-font-size: 11; -fx-border-color: #BCC1C6; -fx-border-radius: 4; -fx-padding: 8;");
        tf.setMinHeight(32);
        return tf;
    }
    
    /**
     * Creates a form group (label + control) with proper alignment.
     */
    public static javafx.scene.layout.HBox createFormGroup(String label, javafx.scene.Node control) {
        javafx.scene.layout.HBox group = new javafx.scene.layout.HBox(10);
        group.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        javafx.scene.control.Label lbl = new javafx.scene.control.Label(label);
        lbl.setStyle(LABEL_STYLE + "; -fx-min-width: 200;");
        javafx.scene.layout.HBox.setHgrow(control, javafx.scene.layout.Priority.ALWAYS);
        group.getChildren().addAll(lbl, control);
        return group;
    }
}
