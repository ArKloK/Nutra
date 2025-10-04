package com.arklok.nutra;

import com.arklok.nutra.config.FxmlView;
import com.arklok.nutra.config.StageManager;
import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class NutraFxApplication extends Application {

    private static Stage stage;

    private ConfigurableApplicationContext applicationContext;
    private StageManager stageManager;

    @Override
    public void init() {
        this.applicationContext = new SpringApplicationBuilder(Main.class).run();
    }

    @Override
    public void stop() {
        applicationContext.close();
        stage.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stageManager = applicationContext.getBean(StageManager.class, primaryStage);
        showLoginScene();
    }

    private void showLoginScene() {
        stageManager.switchScene(FxmlView.HOME);
    }
}
