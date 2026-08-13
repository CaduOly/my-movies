package com.seu.catalog.infra;

import com.seu.catalog.service.CatalogService;
import com.seu.catalog.service.FakeMovieMetadataProvider;
import com.seu.catalog.service.MovieMetadataProvider;
import com.seu.catalog.service.TmdbMetadataProvider;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import com.seu.catalog.dao.MySqlMediaItemDAO;

/**
 * Listener que executa na inicialização da aplicação.
 * Responsabilidades:
 * - Executar Flyway (migrations)
 * - Construir o grafo de dependências (injeção manual)
 * - Logar a URL de acesso
 */
@WebListener
public class AppBootstrap implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(AppBootstrap.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 1. Testar conexão com o banco
            LOG.info("Testando conexão com o banco de dados...");
            try (Connection conn = ConnectionFactory.get()) {
                LOG.info("✓ Conexão bem-sucedida");
            }

            // 2. Executar Flyway
            LOG.info("Executando Flyway (migrations)...");
            String dbUrl = System.getenv("JDBC_URL") != null 
                ? System.getenv("JDBC_URL")
                : "jdbc:mysql://localhost:3306/my_movies?useUnicode=true&characterEncoding=UTF-8";
            String dbUser = System.getenv("JDBC_USER") != null 
                ? System.getenv("JDBC_USER")
                : "app";
            String dbPass = System.getenv("JDBC_PASSWORD") != null 
                ? System.getenv("JDBC_PASSWORD")
                : "app123";

            Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPass)
                .load();

            int migrationsApplied = flyway.migrate().migrationsExecuted;
            LOG.info("✓ Flyway: " + migrationsApplied + " migration(s) aplicada(s)");

            var dao = new MySqlMediaItemDAO();
            
            String apiKey = System.getenv("TMDB_API_KEY");
            MovieMetadataProvider metadataProvider;
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                metadataProvider = new TmdbMetadataProvider();
            } else {
                metadataProvider = new FakeMovieMetadataProvider();
            }

            var service = new CatalogService(dao, metadataProvider);
            sce.getServletContext().setAttribute("catalogService", service);
            sce.getServletContext().setAttribute("metadataProvider", metadataProvider);

            String contextPath = sce.getServletContext().getContextPath();
            String appUrl = "http://localhost:8080" + (contextPath.isEmpty() ? "/" : contextPath);
            LOG.info("═══════════════════════════════════════════════════════════");
            LOG.info("✓ Aplicação pronta!");
            LOG.info("✓ Acesso: " + appUrl);
            LOG.info("═══════════════════════════════════════════════════════════");

        } catch (Exception e) {
            LOG.severe("Erro na inicialização: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Aplicação finalizada");
    }
}
