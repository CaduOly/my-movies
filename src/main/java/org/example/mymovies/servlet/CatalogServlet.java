package org.example.mymovies.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mymovies.exception.ServiceException;
import org.example.mymovies.model.MediaItem;
import org.example.mymovies.model.MediaType;
import org.example.mymovies.service.CatalogService;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/items"})
public class CatalogServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "manage";
        }

        CatalogService service = (CatalogService) getServletContext().getAttribute("catalogService");

        try {
            switch (action) {
                case "new":
                    req.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(req, resp);
                    break;
                case "edit":
                    Long id = Long.parseLong(req.getParameter("id"));
                    MediaItem item = service.findById(id);
                    req.setAttribute("item", item);
                    req.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(req, resp);
                    break;
                case "detail":
                    Long detailId = Long.parseLong(req.getParameter("id"));
                    MediaItem detailItem = service.findById(detailId);
                    req.setAttribute("item", detailItem);
                    req.getRequestDispatcher("/WEB-INF/views/detail.jsp").forward(req, resp);
                    break;
                case "search":
                    String term = req.getParameter("term");
                    List<MediaItem> searchResults = service.searchByTerm(term);
                    req.setAttribute("items", searchResults);
                    req.getRequestDispatcher("/WEB-INF/views/manage.jsp").forward(req, resp);
                    break;
                case "manage":
                default:
                    List<MediaItem> items = service.findAll();
                    req.setAttribute("items", items);
                    req.getRequestDispatcher("/WEB-INF/views/manage.jsp").forward(req, resp);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        CatalogService service = (CatalogService) getServletContext().getAttribute("catalogService");

        try {
            if ("delete".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                service.deleteMediaItem(id);
                resp.sendRedirect(req.getContextPath() + "/items?action=manage");
            } else if ("save".equals(action)) {
                String idStr = req.getParameter("id");
                String title = req.getParameter("title");
                String description = req.getParameter("description");
                String typeStr = req.getParameter("type");
                String releaseYearStr = req.getParameter("releaseYear");
                String authorDirector = req.getParameter("authorDirector");
                String genre = req.getParameter("genre");

                MediaItem item = new MediaItem();
                if (idStr != null && !idStr.trim().isEmpty()) {
                    item.setId(Long.parseLong(idStr));
                }
                item.setTitle(title);
                item.setDescription(description);
                item.setAuthorDirector(authorDirector);
                item.setGenre(genre);
                if (typeStr != null && !typeStr.isEmpty()) {
                    item.setType(MediaType.valueOf(typeStr));
                }
                
                try {
                    if (releaseYearStr != null && !releaseYearStr.trim().isEmpty()) {
                        item.setReleaseYear(Integer.parseInt(releaseYearStr));
                    }
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "Invalid release year format");
                    req.setAttribute("item", item);
                    req.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(req, resp);
                    return;
                }

                try {
                    if (item.getId() != null) {
                        service.updateMediaItem(item);
                    } else {
                        service.addMediaItem(item);
                    }
                    resp.sendRedirect(req.getContextPath() + "/items?action=manage");
                } catch (ServiceException e) {
                    req.setAttribute("error", e.getMessage());
                    req.setAttribute("item", item);
                    req.getRequestDispatcher("/WEB-INF/views/form.jsp").forward(req, resp);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
