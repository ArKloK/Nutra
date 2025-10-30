package com.arklok.nutra.config;

import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class PrimaryStageHolder {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        PrimaryStageHolder.primaryStage = primaryStage;
    }

    public boolean isReady() {
        return primaryStage != null;
    }
}