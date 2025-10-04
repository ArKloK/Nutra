package com.arklok.nutra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.driver-class-name:org.sqlite.JDBC}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        // Comprueba propiedad
        if (datasourceUrl == null || datasourceUrl.isBlank()) {
            throw new IllegalStateException("spring.datasource.url no está configurada. Comprueba application.properties");
        }

        // Si es SQLite con fichero, crea directorio padre si hace falta
        if (datasourceUrl.startsWith("jdbc:sqlite:")) {
            String filePart = datasourceUrl.substring("jdbc:sqlite:".length());
            try {
                Path dbFile = Paths.get(filePart);
                Path parent = dbFile.getParent();
                if (parent != null && !Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                // Nota: SQLite crea el fichero si no existe cuando se conecta
            } catch (Exception e) {
                throw new RuntimeException("No se pudo preparar el directorio para la BBDD SQLite: " + e.getMessage(), e);
            }
        }

        // Construye DataSource
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(datasourceUrl)
                .build();
    }
}
