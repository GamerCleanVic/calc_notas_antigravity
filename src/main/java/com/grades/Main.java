package com.grades;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private TextField n1Field, n2Field, n3Field, n4Field, finalField;
    private Button calcButton, verifyFinalButton;
    private double dife;

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

    private void calculateAverage() {
        try {
            double n1 = Double.parseDouble(n1Field.getText());
            double n2 = Double.parseDouble(n2Field.getText());
            double n3 = Double.parseDouble(n3Field.getText());
            double n4 = Double.parseDouble(n4Field.getText());

            double average = (n1 + n2 + n3 + n4) / 4.0;

            StringBuffer sb = new StringBuffer();
            if (average >= 7.0) {
                sb.append("Sua média é ").append(String.format("%.2f", average))
                        .append(". Nota >= 7 - Aprovado");
                showAlert(Alert.AlertType.INFORMATION, "Aprovado", sb.toString());

                resetFinal();
            } else if (average < 5.0) {
                // Modified Logic: Show Average and Reproval message
                sb.append("Sua média é ").append(String.format("%.2f", average))
                        .append(".\nAluno reprovado.");
                showAlert(Alert.AlertType.ERROR, "Reprovado", sb.toString());

                resetFinal();
            } else {
                dife = 10.0 - average;
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

    private void resetFinal() {
        finalField.setDisable(true);
        finalField.clear();
        verifyFinalButton.setDisable(true);
    }

    private void checkFinal() {
        try {
            double finalGrade = Double.parseDouble(finalField.getText());
            StringBuffer sb = new StringBuffer();

            if (finalGrade >= dife) {
                sb.append("Aluno aprovado");
                showAlert(Alert.AlertType.INFORMATION, "Aprovado", sb.toString());
            } else {
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

        // Use TextFlow for rich text styling
        TextFlow textFlow = createStyledTextFlow(content);
        alert.getDialogPane().setContent(textFlow);

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

    private TextFlow createStyledTextFlow(String content) {
        TextFlow flow = new TextFlow();
        // Regex to match numbers (including decimals with . or ,) or specific keywords
        // Keywords: aprovado, reprovado (case insensitive)
        // Numbers: \d+([.,]\d+)?
        String regex = "(?i)(\\baprovado\\b|\\breprovado\\b|\\d+([.,]\\d+)?)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        int lastEnd = 0;
        while (matcher.find()) {
            // Append text before the match
            if (matcher.start() > lastEnd) {
                Text plainText = new Text(content.substring(lastEnd, matcher.start()));
                flow.getChildren().add(plainText);
            }

            // Append the match with highlight style
            Text highlightedText = new Text(matcher.group());
            highlightedText.getStyleClass().add("highlight-text");
            flow.getChildren().add(highlightedText);

            lastEnd = matcher.end();
        }

        // Append remaining text
        if (lastEnd < content.length()) {
            Text plainText = new Text(content.substring(lastEnd));
            flow.getChildren().add(plainText);
        }

        return flow;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
