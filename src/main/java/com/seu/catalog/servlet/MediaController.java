package com.seu.catalog.servlet;

import com.seu.catalog.service.CatalogService;
import com.seu.catalog.service.MovieMetadataProvider;
import com.seu.catalog.exception.ServiceException;
import com.seu.catalog.exception.ValidationException;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servlet controlador principal da aplicação.
 * Recebe requisição HTTP, coordena Service, escolhe view.
 * Responsabilidades: orquestração APENAS.
 */
@WebServlet(urlPatterns = {"/app/*"})
public class MediaController extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(MediaController.class.getName());
    
    /** Serviço principal de catálogo */
    private CatalogService service;
    
    /** Provedor de metadados para busca no TMDB */
    private MovieMetadataProvider metadataProvider;

    /**
     * Inicializa dependências do servlet.
     * @throws ServletException em caso de erro na inicialização
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.service = (CatalogService) getServletContext().getAttribute("catalogService");
        this.metadataProvider = (MovieMetadataProvider) getServletContext().getAttribute("metadataProvider");
        
        if (this.service == null || this.metadataProvider == null) {
            throw new ServletException("Dependências não inicializadas no ServletContext");
        }
    }

    /**
     * Trata requisições GET.
     * @param req a requisição
     * @param resp a resposta
     * @throws ServletException em caso de erro de servlet
     * @throws IOException em caso de erro de I/O
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = extractAction(req);

        try {
            switch (action) {
                case "home":
                    handleHome(req, resp);
                    break;
                case "list":
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
                case "tmdb-search":
                    handleTmdbSearch(req, resp);
                    break;
                case "about":
                    handleAbout(req, resp);
                    break;
                default:
                    resp.sendError(404);
            }
        } catch (ServiceException e) {
            LOG.warning("Erro no serviço: " + e.getMessage());
            req.setAttribute("errorKey", "error.db_error");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Trata requisições POST.
     * @param req a requisição
     * @param resp a resposta
     * @throws ServletException em caso de erro de servlet
     * @throws IOException em caso de erro de I/O
     */
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
                case "rate":
                    handleRate(req, resp);
                    break;
                default:
                    resp.sendError(404);
            }
        } catch (ServiceException | ValidationException | IllegalArgumentException e) {
            LOG.warning("Erro de validação/serviço: " + e.getMessage());
            req.setAttribute("errorKey", e.getMessage() != null && e.getMessage().startsWith("error.") ? e.getMessage() : "error.validation");
            
            MediaItem partialItem = new MediaItem();
            partialItem.setTitle(req.getParameter("title"));
            try {
                String type = req.getParameter("mediaType");
                if (type != null && !type.isBlank()) {
                    partialItem.setMediaType(MediaType.valueOf(type));
                }
            } catch (Exception ex) {}
            String releaseYear = req.getParameter("releaseYear");
            if (releaseYear != null && !releaseYear.isEmpty()) {
                try { partialItem.setReleaseYear(Integer.parseInt(releaseYear)); } catch (Exception ex) {}
            }
            partialItem.setAuthorDirector(req.getParameter("authorDirector"));
            partialItem.setGenre(req.getParameter("genre"));
            partialItem.setSynopsis(req.getParameter("synopsis"));
            partialItem.setPosterUrl(req.getParameter("posterUrl"));
            partialItem.setComment(req.getParameter("comment"));
            String rating = req.getParameter("rating");
            if (rating != null && !rating.isEmpty()) {
                try { partialItem.setRating(Integer.parseInt(rating)); } catch (Exception ex) {}
            }
            if ("update".equals(action)) {
                partialItem.setId(parseId(req.getParameter("id")));
            }
            
            req.setAttribute("item", partialItem);
            req.setAttribute("isEdit", "update".equals(action));
            req.getRequestDispatcher("/WEB-INF/jsp/form.jsp").forward(req, resp);
        }
    }

    /**
     * Renderiza a página inicial.
     * @param req a requisição
     * @param resp a resposta
     * @throws ServiceException em caso de erro no serviço
     * @throws ServletException em caso de erro de servlet
     * @throws IOException em caso de erro de I/O
     */
    private void handleHome(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        List<MediaItem> items = service.listAllItems();
        req.setAttribute("items", items);
        req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
    }

    /**
     * Renderiza a lista de itens.
     */
    private void handleList(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        List<MediaItem> items = service.listAllItems();
        req.setAttribute("items", items);
        req.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(req, resp);
    }

    /**
     * Renderiza os detalhes de um item.
     */
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

    /**
     * Renderiza o formulário para um novo item.
     */
    private void handleNewForm(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.setAttribute("item", null);
        req.setAttribute("isEdit", false);
        req.getRequestDispatcher("/WEB-INF/jsp/form.jsp").forward(req, resp);
    }

    /**
     * Renderiza o formulário para editar um item existente.
     */
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

    /**
     * Trata a busca interna de itens.
     */
    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ServletException, IOException {
        String term = req.getParameter("term");
        if (term == null || term.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/app/home");
            return;
        }

        List<MediaItem> results = service.searchItems(term);
        req.setAttribute("items", results);
        req.setAttribute("searchTerm", term);
        req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
    }

    /**
     * Trata a busca no TMDB retornando JSON.
     */
    private void handleTmdbSearch(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        String term = req.getParameter("term");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        if (term == null || term.trim().isEmpty()) {
            resp.getWriter().write("[]");
            return;
        }

        List<MediaItem> items = metadataProvider.searchByTitle(term);
        if (items == null || items.isEmpty()) {
            resp.getWriter().write("[]");
            return;
        }
        
        JSONArray jsonArray = new JSONArray();
        for (MediaItem item : items) {
            JSONObject json = new JSONObject();
            json.put("title", item.getTitle() != null ? item.getTitle() : "");
            json.put("releaseYear", item.getReleaseYear() != null ? item.getReleaseYear() : "");
            json.put("genre", item.getGenre() != null ? item.getGenre() : "");
            json.put("authorDirector", item.getAuthorDirector() != null ? item.getAuthorDirector() : "");
            json.put("synopsis", item.getSynopsis() != null ? item.getSynopsis() : "");
            json.put("posterUrl", item.getPosterUrl() != null ? item.getPosterUrl() : "");
            jsonArray.put(json);
        }
        
        resp.getWriter().write(jsonArray.toString());
    }

    /**
     * Lida com a requisição da página Sobre.
     * Renderiza o arquivo about.jsp, não requer banco de dados.
     * 
     * @param req a requisição HTTP
     * @param resp a resposta HTTP
     * @throws ServletException em caso de erro no servlet
     * @throws IOException em caso de erro de I/O
     */
    private void handleAbout(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        req.setAttribute("appVersion", "1.0.0");
        req.getRequestDispatcher("/WEB-INF/jsp/about.jsp").forward(req, resp);
    }

    /**
     * Salva um novo item.
     */
    private void handleSave(HttpServletRequest req, HttpServletResponse resp) 
            throws ValidationException, ServiceException, IOException {
        MediaItem item = extractMediaItemFromRequest(req, false);
        service.createItem(item);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    /**
     * Atualiza um item existente.
     */
    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) 
            throws ValidationException, ServiceException, IOException {
        MediaItem item = extractMediaItemFromRequest(req, true);
        MediaItem existing = service.getItemById(item.getId());
        if (existing == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        item.setRating(existing.getRating());
        item.setComment(existing.getComment());
        service.updateItem(item);
        resp.sendRedirect(req.getContextPath() + "/app/list");
    }

    /**
     * Exclui um item.
     */
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

    /**
     * Avalia um item.
     */
    private void handleRate(HttpServletRequest req, HttpServletResponse resp) 
            throws ServiceException, ValidationException, IOException {
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

        String ratingStr = req.getParameter("rating");
        if (ratingStr != null && !ratingStr.isEmpty()) {
            item.setRating(Integer.parseInt(ratingStr));
        }

        String comment = req.getParameter("comment");
        if (comment != null) {
            item.setComment(comment.trim().isEmpty() ? null : comment);
        }

        service.updateItem(item);
        resp.sendRedirect(req.getContextPath() + "/app/detail?id=" + id);
    }

    /**
     * Extrai a ação a partir da URL.
     */
    private String extractAction(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return "list";
        }
        return pathInfo.replaceFirst("^/", "").split("\\?")[0];
    }

    /**
     * Converte a string do ID para Integer.
     */
    private Integer parseId(String idStr) {
        if (idStr == null) return null;
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Extrai os dados da requisição para um MediaItem.
     */
    private MediaItem extractMediaItemFromRequest(HttpServletRequest req, boolean isEdit) {
        MediaItem item = new MediaItem();
        item.setTitle(req.getParameter("title"));
        
        String mediaTypeParam = req.getParameter("mediaType");
        if (mediaTypeParam == null || mediaTypeParam.isBlank()) {
            throw new IllegalArgumentException("error.type_required");
        }
        try {
            item.setMediaType(MediaType.valueOf(mediaTypeParam));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("error.type_required", e);
        }

        if (isEdit) {
            item.setId(parseId(req.getParameter("id")));
        }

        String releaseYear = req.getParameter("releaseYear");
        if (releaseYear != null && !releaseYear.isEmpty()) {
            try {
                item.setReleaseYear(Integer.parseInt(releaseYear));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("error.invalid_year", e);
            }
        }

        item.setAuthorDirector(req.getParameter("authorDirector"));
        item.setGenre(req.getParameter("genre"));
        item.setSynopsis(req.getParameter("synopsis"));
        item.setPosterUrl(req.getParameter("posterUrl"));


        String rating = req.getParameter("rating");
        if (rating != null && !rating.isEmpty()) {
            try {
                item.setRating(Integer.parseInt(rating));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("error.invalid_rating", e);
            }
        }

        item.setComment(req.getParameter("comment"));

        return item;
    }
}
