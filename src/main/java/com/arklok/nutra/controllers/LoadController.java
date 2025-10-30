package com.arklok.nutra.controllers;

import com.arklok.nutra.config.FxmlView;
import com.arklok.nutra.config.StageManager;
import com.arklok.nutra.constants.UIConstants;
import com.arklok.nutra.helpers.UIHelper;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class LoadController {
    @FXML
    public Circle leftCircle;

    @FXML
    public Circle rightCircle;

    @FXML
    private StackPane rootPane;

    @FXML
    private Text appNameText;

    @FXML
    private Text taglineText;

    @FXML
    private ProgressBar loadingBar;

    @FXML
    private Text loadingText;

    private final StageManager stageManager;

    public LoadController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    public void initialize() {

        UIHelper.InitializeUI();

        rootPane.setStyle(UIConstants.STYLE_BACKGROUND);
        appNameText.setText(UIConstants.APP_NAME);
        taglineText.setText(UIConstants.APP_TAGLINE);
        loadingText.setText(UIConstants.LOADING_TEXT);

        // Make the progress bar indeterminate
        loadingBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        // Start circle animations
        animateCircles();

        // Start loading process
        startLoadingProcess();
    }

    private void startLoadingProcess() {
        Task<Void> loadingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simular la carga de la aplicación
                // Aquí puedes añadir lógica real de inicialización
                Thread.sleep(3000); // 3 segundos de carga mínima
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // Cambiar a la pantalla principal cuando termine la carga
                Platform.runLater(() -> {
                    stageManager.switchScene(FxmlView.HOME);
                });
            }

            @Override
            protected void failed() {
                super.failed();
                // En caso de error, también ir a HOME
                Platform.runLater(() -> {
                    stageManager.switchScene(FxmlView.HOME);
                });
            }
        };

        // Ejecutar la tarea en un hilo separado
        Thread loadingThread = new Thread(loadingTask);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private void animateCircles() {
        // Left circle animation
        FadeTransition fadeOutLeft = new FadeTransition(Duration.seconds(3), leftCircle);
        fadeOutLeft.setFromValue(0.1);
        fadeOutLeft.setToValue(0.03);

        FadeTransition fadeInLeft = new FadeTransition(Duration.seconds(3), leftCircle);
        fadeInLeft.setFromValue(0.03);
        fadeInLeft.setToValue(0.1);

        TranslateTransition moveLeft = new TranslateTransition(Duration.seconds(6), leftCircle);
        moveLeft.setByX(100);
        moveLeft.setByY(50);
        moveLeft.setAutoReverse(true);
        moveLeft.setCycleCount(TranslateTransition.INDEFINITE);

        // Right circle animation
        FadeTransition fadeOutRight = new FadeTransition(Duration.seconds(4), rightCircle);
        fadeOutRight.setFromValue(0.08);
        fadeOutRight.setToValue(0.02);

        FadeTransition fadeInRight = new FadeTransition(Duration.seconds(4), rightCircle);
        fadeInRight.setFromValue(0.02);
        fadeInRight.setToValue(0.08);

        TranslateTransition moveRight = new TranslateTransition(Duration.seconds(8), rightCircle);
        moveRight.setByX(-80);
        moveRight.setByY(-60);
        moveRight.setAutoReverse(true);
        moveRight.setCycleCount(TranslateTransition.INDEFINITE);

        // Fade sequences for breathing effect
        SequentialTransition breatheLeft = new SequentialTransition(fadeOutLeft, fadeInLeft);
        breatheLeft.setCycleCount(SequentialTransition.INDEFINITE);

        SequentialTransition breatheRight = new SequentialTransition(fadeOutRight, fadeInRight);
        breatheRight.setCycleCount(SequentialTransition.INDEFINITE);

        // Start all animations in parallel
        breatheLeft.play();
        breatheRight.play();
        moveLeft.play();
        moveRight.play();
    }
}
