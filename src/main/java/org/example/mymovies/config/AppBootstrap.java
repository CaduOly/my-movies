package org.example.mymovies.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.mymovies.service.CatalogService;
import org.example.mymovies.validator.MediaItemValidator;

@WebListener
public class AppBootstrap implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing AppBootstrap...");

        // Create dependencies
        MediaItemValidator validator = new MediaItemValidator();
        // Since MovieMetadataProvider is implemented in Task 4, we pass null for now (DIP)
        CatalogService catalogService = new CatalogService(validator, null);

        // Put the service in the ServletContext so Servlets can access it
        sce.getServletContext().setAttribute("catalogService", catalogService);
        
        System.out.println("AppBootstrap initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("AppBootstrap destroyed.");
    }
}
