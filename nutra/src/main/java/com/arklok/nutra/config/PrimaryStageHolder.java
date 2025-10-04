package com.arklok.nutra.config;

import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class PrimaryStageHolder {

    private Stage primaryStage;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public boolean isReady() {
        return primaryStage != null;
    }
}