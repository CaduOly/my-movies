package com.seu.catalog.servlet;

import com.seu.catalog.infra.ConnectionFactory;
import com.seu.catalog.service.*;
import com.seu.catalog.exception.*;
import com.seu.catalog.model.*;
import com.seu.catalog.dao.MySqlMediaItemDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet controlador principal da aplicação.
 * Recebe requisição HTTP, coordena Service, escolhe view.
 * Responsabilidades: orquestração APENAS.
 */
@WebServlet(urlPatterns = {"/", "/app/*"})
public class MediaController extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(MediaController.class.getName());
    
    private CatalogService service;

    @Override
    public void init() throws ServletException {
        super.init();
        var dao = new MySqlMediaItemDAO();
        var provider = new FakeMovieMetadataProvider();
        this.service = new CatalogService(dao, provider);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = extractAction(req);

        try {
            switch (action) {
                case "list":
                case "":
                    handleList(req, resp);
                    break;
                case "detail":
                    handleDetail(req, resp);
                    break;
                case "new":
                    handleNewForm(req, resp);
                    break;
                case "edit":
                    handleEditForm(req, resp);
                    break;
                case "search":
                    handleSearch(req, resp);
                    break;
                default:
                    resp.sendError(404);
            }
        } catch (ServiceException e) {
            LOG.warning("Erro no serviço: " + e.getMessage());
            req.setAttribute("error", "Erro ao processar requisição");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = extractAction(req);

        try {
            switch (action) {
                case "save":
                    handleSave(req, resp);
                    break;
                case "update":
                    handleUpdate(req, resp);
                    break;
                case "delete":
                    handleDelete(req, resp);
                    break;
                default:
                    resp.sendError(404);
            }
        } catch (ServiceException | ValidationException e) {
            LOG.warning("Erro: " + e.getMessage());
            req.setAttribute("error", e.getMessage());
            handleNewForm(req, resp);
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        List<MediaItem> items = service.listAllItems();
        req.setAttribute("items", items);
        req.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(req, resp);
    }

    private void handleDetail(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendError(400);
            return;
        }

        MediaItem item = service.getItemById(id);
        if (item == null) {
            resp.sendError(404);
            return;
        }

        req.setAttribute("item", item);
        req.getRequestDispatcher("/WEB-INF/jsp/detail.jsp").forward(req, resp);
    }

    private void handleNewForm(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.setAttribute("item", null);
        req.setAttribute("isEdit", false);
        req.getRequestDispatcher("/WEB-INF/jsp/form.jsp").forward(req, resp);
    }

    private void handleEditForm(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendError(400);
            return;
        }

        MediaItem item = service.getItemById(id);
        if (item == null) {
            resp.sendError(404);
            return;
        }

        req.setAttribute("item", item);
        req.setAttribute("isEdit", true);
        req.getRequestDispatcher("/WEB-INF/jsp/form.jsp").forward(req, resp);
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        String term = req.getParameter("term");
        if (term == null || term.trim().isEmpty()) {
            resp.sendError(400);
            return;
        }

        List<MediaItem> results = service.searchItems(term);
        req.setAttribute("items", results);
        req.setAttribute("searchTerm", term);
        req.getRequestDispatcher("/WEB-INF/jsp/search-results.jsp").forward(req, resp);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) 
            throws ValidationException, ServiceException, IOException {
        MediaItem item = extractMediaItemFromRequest(req, false);
        service.createItem(item);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) 
            throws ValidationException, ServiceException, IOException {
        MediaItem item = extractMediaItemFromRequest(req, true);
        service.updateItem(item);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, IOException {
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            resp.sendError(400);
            return;
        }

        service.deleteItem(id);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    private String extractAction(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        return pathInfo.replaceFirst("^/", "").split("\\?")[0];
    }

    private Integer parseId(String idStr) {
        if (idStr == null) return null;
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private MediaItem extractMediaItemFromRequest(HttpServletRequest req, boolean isEdit) {
        MediaItem item = new MediaItem(
            req.getParameter("title"),
            MediaType.valueOf(req.getParameter("mediaType"))
        );

        if (isEdit) {
            item.setId(parseId(req.getParameter("id")));
        }

        String releaseYear = req.getParameter("releaseYear");
        if (releaseYear != null && !releaseYear.isEmpty()) {
            try {
                item.setReleaseYear(Integer.parseInt(releaseYear));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ano inválido", e);
            }
        }

        item.setAuthorDirector(req.getParameter("authorDirector"));
        item.setGenre(req.getParameter("genre"));
        item.setSynopsis(req.getParameter("synopsis"));
        item.setPosterUrl(req.getParameter("posterUrl"));
        item.setExternalId(req.getParameter("externalId"));

        String rating = req.getParameter("rating");
        if (rating != null && !rating.isEmpty()) {
            try {
                item.setRating(Integer.parseInt(rating));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Avaliação inválida", e);
            }
        }

        item.setComment(req.getParameter("comment"));

        return item;
    }
}
