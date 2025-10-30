package com.arklok.nutra;

import com.arklok.nutra.config.FxmlView;
import com.arklok.nutra.config.PrimaryStageHolder;
import com.arklok.nutra.config.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
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

        stageManager = applicationContext.getBean(StageManager.class);
        stageManager.setPrimaryStage(primaryStage);

        showLoadScreen();
    }

    private void showLoadScreen() {
        stageManager.switchScene(FxmlView.LOAD);
    }

    @Override
    public void stop() {
        try {
//            PrimaryStageHolder holder = applicationContext.getBean(PrimaryStageHolder.class);
            Stage primary = PrimaryStageHolder.getPrimaryStage();
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
