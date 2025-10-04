package com.arklok.nutra.config;

import com.arklok.nutra.event.SceneResizeEvent;
import javafx.stage.Stage;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.util.Objects;

public class StageManager {
    private final Stage primaryStage;
    private final FxmlLoader fxmlLoader;
    private final String applicationTitle;
    private final ApplicationEventPublisher eventPublisher;

    public StageManager(FxmlLoader fxmlLoader,
                        Stage primaryStage,
                        ApplicationEventPublisher eventPublisher) {
        this.primaryStage = Objects.requireNonNull(primaryStage, "primaryStage cannot be null");
        this.fxmlLoader = Objects.requireNonNull(fxmlLoader, "fxmlLoader cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher cannot be null");
        this.applicationTitle = "Nutra - Gestión de Clientes";
    }

    public void switchScene(final FxmlView view) {
        primaryStage.setMinWidth(1010);
        primaryStage.setMinHeight(700);
        primaryStage.setTitle(applicationTitle);

        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        Parent rootNode = loadRootNode(view.getFxmlPath());

        Scene scene = new Scene(rootNode);

        String stylesheet = Objects.requireNonNull(
                getClass().getResource("/styles/styles.css"),
                "styles.css not found in /styles/"
        ).toExternalForm();

        scene.getStylesheets().add(stylesheet);

        scene.widthProperty().addListener((ObservableValue<? extends Number> _,
                                           Number _,
                                           Number newSceneWidth) ->
                eventPublisher.publishEvent(new SceneResizeEvent(this, newSceneWidth))
        );

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void switchToNextScene(final FxmlView view) {
        Parent rootNode = loadRootNode(view.getFxmlPath());
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(rootNode));
        } else {
            primaryStage.getScene().setRoot(rootNode);
        }
        primaryStage.show();
    }

    private Parent loadRootNode(String fxmlPath) {
        try {
            Parent rootNode = fxmlLoader.load(fxmlPath);
            return Objects.requireNonNull(rootNode, "loaded FXML is null: " + fxmlPath);
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML: " + fxmlPath, e);
        }
    }

    public void switchToFullScreenMode() {
        primaryStage.setFullScreen(true);
    }

    public void switchToWindowedMode() {
        primaryStage.setFullScreen(false);
    }

    public boolean isStageFullScreen() {
        return primaryStage.isFullScreen();
    }

    public void exit() {
        primaryStage.close();
    }
}
