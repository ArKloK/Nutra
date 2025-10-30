package com.arklok.nutra.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class FxmlLoader {
    private final ApplicationContext context;

    public FxmlLoader(ApplicationContext context) {
        this.context = context;
    }

    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);

        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalArgumentException("FXML not found: " + fxmlPath);
        }
        loader.setLocation(resource);

        return loader.load();
    }
}
