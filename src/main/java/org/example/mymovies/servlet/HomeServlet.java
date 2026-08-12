package org.example.mymovies.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mymovies.service.CatalogService;

import java.io.IOException;

@WebServlet(urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {
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
