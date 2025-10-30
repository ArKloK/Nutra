package com.arklok.nutra;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NutraApplication{
    static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(NutraFxApplication.class)
                .headless(false)
                .run(args);

        Application.launch(NutraFxApplication.class, args);

        ctx.close();
    }
}
