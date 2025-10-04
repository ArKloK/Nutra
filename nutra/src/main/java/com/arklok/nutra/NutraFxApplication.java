package com.arklok.nutra;

import com.arklok.nutra.config.FxmlLoader;
import com.arklok.nutra.config.FxmlView;
import com.arklok.nutra.config.PrimaryStageHolder;
import com.arklok.nutra.config.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class NutraFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private StageManager stageManager;

    @Override
    public void init() {
        this.applicationContext = new SpringApplicationBuilder(NutraApplication.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        PrimaryStageHolder holder = applicationContext.getBean(PrimaryStageHolder.class);
        holder.setPrimaryStage(primaryStage);

        FxmlLoader fxmlLoader = applicationContext.getBean(FxmlLoader.class);
        ApplicationEventPublisher eventPublisher = applicationContext;

        stageManager = new StageManager(fxmlLoader, primaryStage, eventPublisher);

        showLoginScene();
    }

    private void showLoginScene() {
        stageManager.switchScene(FxmlView.HOME);
    }

    @Override
    public void stop() {
        try {
            PrimaryStageHolder holder = applicationContext.getBean(PrimaryStageHolder.class);
            Stage primary = holder.getPrimaryStage();
            if (primary != null) {
                primary.close();
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Error while closing te app", e);
        } finally {
            if (applicationContext != null) {
                applicationContext.close();
            }
        }
    }
}
