package org.example.mymovies.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.mymovies.service.CatalogService;
import org.example.mymovies.validator.MediaItemValidator;

/**
 * Classe responsável por inicializar componentes e dependências no momento 
 * em que a aplicação web é iniciada.
 */
@WebListener
public class AppBootstrap implements ServletContextListener {

    /**
     * Método chamado quando o contexto do servlet é inicializado.
     * Instancia as dependências e as armazena no contexto.
     * 
     * Como o provedor de metadados será implementado posteriormente, 
     * passamos null por enquanto (Inversão de Dependência).
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing AppBootstrap...");

        MediaItemValidator validator = new MediaItemValidator();
        CatalogService catalogService = new CatalogService(validator, null);

        sce.getServletContext().setAttribute("catalogService", catalogService);
        
        System.out.println("AppBootstrap initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("AppBootstrap destroyed.");
    }
}
