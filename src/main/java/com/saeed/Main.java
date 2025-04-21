package com.saeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Main application class that configures Spring Boot and Vaadin.
 * The application provides both a REST API and a Vaadin-based UI.
 */
@SpringBootApplication
@Theme(value = "lumo", variant = Lumo.LIGHT)
public class Main implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Employee Service Application with Vaadin UI Started!");
    }
}
