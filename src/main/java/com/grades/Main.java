package com.grades;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private TextField n1Field, n2Field, n3Field, n4Field, finalField;
    private Button calcButton, verifyFinalButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculadora de Notas");

        // Header
        Label titleLabel = new Label("Calculadora de Médias");
        titleLabel.getStyleClass().add("header-label");

        // N1 - N4 Inputs
        HBox gradesBox = new HBox(15);
        gradesBox.setAlignment(Pos.CENTER);
        n1Field = createStyledTextField("N1");
        n2Field = createStyledTextField("N2");
        n3Field = createStyledTextField("N3");
        n4Field = createStyledTextField("N4");
        gradesBox.getChildren().addAll(n1Field, n2Field, n3Field, n4Field);

        // Buttons
        calcButton = new Button("Calcular Média");
        calcButton.getStyleClass().add("primary-button");
        calcButton.setOnAction(e -> calculateAverage());

        // Final Grade Section (initially hidden/disabled visual cue)
        VBox finalSection = new VBox(10);
        finalSection.setAlignment(Pos.CENTER);
        finalSection.getStyleClass().add("final-section");

        Label finalLabel = new Label("Nota Final");
        finalLabel.getStyleClass().add("sub-header");

        // Final input input separated
        finalField = createStyledTextField("Final");
        finalField.setDisable(true);

        verifyFinalButton = new Button("Verificar Final");
        verifyFinalButton.getStyleClass().add("secondary-button");
        verifyFinalButton.setDisable(true);
        verifyFinalButton.setOnAction(e -> checkFinal());

        finalSection.getChildren().addAll(finalLabel, finalField, verifyFinalButton);

        // Main Layout
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-pane");
        root.getChildren().addAll(titleLabel, gradesBox, calcButton, finalSection);

        Scene scene = new Scene(root, 500, 450);

        // Load CSS
        String css = getClass().getResource("/style.css") != null
                ? getClass().getResource("/style.css").toExternalForm()
                : null;
        if (css != null) {
            scene.getStylesheets().add(css);
        } else {
            // Fallback if css file resource finding fails (though we will create it)
            // In a real jar run, resources need to be handled carefully, but for local run
            // it should work if in classpath.
            // We will handle classpath in run script.
            System.out.println("CSS not found, running without styles.");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createStyledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(70);
        tf.getStyleClass().add("grade-field");
        return tf;
    }

    private double dife;

    private void calculateAverage() {
        try {
            double n1 = Double.parseDouble(n1Field.getText());
            double n2 = Double.parseDouble(n2Field.getText());
            double n3 = Double.parseDouble(n3Field.getText());
            double n4 = Double.parseDouble(n4Field.getText());

            double average = (n1 + n2 + n3 + n4) / 4.0;

            if (average >= 7.0) {
                StringBuffer sb = new StringBuffer();
                sb.append("Sua média é ").append(String.format("%.2f", average))
                        .append(". Nota >= 7 - Aprovado");
                showAlert(Alert.AlertType.INFORMATION, "Aprovado", sb.toString());

                // Reset/Disable final if previously enabled
                finalField.setDisable(true);
                finalField.clear();
                verifyFinalButton.setDisable(true);
            } else if (average < 5.0) {
                showAlert(Alert.AlertType.ERROR, "Reprovado", "Média < 5. Aluno reprovado!");
                finalField.setDisable(true);
                finalField.clear();
                verifyFinalButton.setDisable(true);
            } else {
                dife = 10.0 - average;
                StringBuffer sb = new StringBuffer();
                sb.append("Sua média é ").append(String.format("%.2f", average))
                        .append(".\nFará a final por ").append(String.format("%.2f", dife));
                showAlert(Alert.AlertType.WARNING, "Recuperação", sb.toString());

                finalField.setDisable(false);
                verifyFinalButton.setDisable(false);
                finalField.requestFocus();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Por favor, insira números válidos.");
        }
    }

    private void checkFinal() {
        try {
            double finalGrade = Double.parseDouble(finalField.getText());

            if (finalGrade >= dife) {
                showAlert(Alert.AlertType.INFORMATION, "Aprovado", "Aluno aprovado");
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append("Nota Final < ").append(String.format("%.2f", dife))
                        .append(". Aluno reprovado.");
                showAlert(Alert.AlertType.ERROR, "Reprovado", sb.toString());
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Por favor, insira uma nota final válida.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Apply styles to dialog
        DialogPane dialogPane = alert.getDialogPane();
        String css = getClass().getResource("/style.css") != null
                ? getClass().getResource("/style.css").toExternalForm()
                : null;
        if (css != null) {
            dialogPane.getStylesheets().add(css);
            dialogPane.getStyleClass().add("custom-alert");
        }

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
