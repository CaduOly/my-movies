package org.example.mymovies.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mymovies.service.CatalogService;

import java.io.IOException;

/**
 * Servlet responsável por exibir a página inicial da aplicação.
 * Carrega a lista de itens do catálogo e a disponibiliza para a view.
 */
@WebServlet(urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {
    
    /**
     * Trata a requisição GET, carregando a lista e encaminhando para a view.
     * 
     * @param req  O objeto HttpServletRequest que contém a requisição do cliente.
     * @param resp O objeto HttpServletResponse que contém a resposta do servlet.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     * @since 1.0
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CatalogService catalogService = (CatalogService) getServletContext().getAttribute("catalogService");
        if (catalogService != null) {
            try {
                req.setAttribute("items", catalogService.findAll());
            } catch (Exception e) {
                req.setAttribute("error", "Error loading catalog.");
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
