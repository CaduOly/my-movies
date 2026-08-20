package com.seu.catalog.servlet;

import com.seu.catalog.exception.ServiceException;
import com.seu.catalog.model.MediaItem;
import com.seu.catalog.model.MediaType;
import com.seu.catalog.service.CatalogService;
import com.seu.catalog.service.MovieMetadataProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes funcionais do Servlet.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MediaController Functional Tests")
class MediaControllerTest {

    @Mock
    private CatalogService serviceMock;
    
    @Mock
    private MovieMetadataProvider metadataProviderMock;

    @Mock
    private ServletConfig servletConfigMock;

    @Mock
    private ServletContext servletContextMock;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private RequestDispatcher requestDispatcherMock;

    private MediaController servlet;

    @BeforeEach
    void setUp() throws ServletException {
        servlet = new MediaController();
        when(servletConfigMock.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getAttribute("catalogService")).thenReturn(serviceMock);
        when(servletContextMock.getAttribute("metadataProvider")).thenReturn(metadataProviderMock);
        
        servlet.init(servletConfigMock);
    }

    @Test
    @DisplayName("deve lidar com home corretamente")
    void testDoGetHome() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/home");
        
        List<MediaItem> items = new ArrayList<>();
        items.add(new MediaItem() {{ setTitle("Test Movie"); setMediaType(MediaType.MOVIE); }});
        when(serviceMock.listAllItems()).thenReturn(items);
        
        when(requestMock.getRequestDispatcher("/WEB-INF/jsp/home.jsp")).thenReturn(requestDispatcherMock);

        servlet.doGet(requestMock, responseMock);

        verify(requestMock).setAttribute("items", items);
        verify(requestDispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    @DisplayName("deve tratar ServiceException enviando para a pagina de erro genérica")
    void testDoGetServiceException() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/list");
        when(serviceMock.listAllItems()).thenThrow(new ServiceException("DB Error", null));
        when(requestMock.getRequestDispatcher("/WEB-INF/jsp/error.jsp")).thenReturn(requestDispatcherMock);

        servlet.doGet(requestMock, responseMock);

        verify(requestMock).setAttribute(eq("errorKey"), anyString());
        verify(requestDispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    @DisplayName("deve lidar com about corretamente sem chamar o service")
    void testDoGetAbout() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/about");
        when(requestMock.getRequestDispatcher("/WEB-INF/jsp/about.jsp")).thenReturn(requestDispatcherMock);

        servlet.doGet(requestMock, responseMock);

        verify(requestMock).setAttribute("appVersion", "1.0.0");
        verify(requestDispatcherMock).forward(requestMock, responseMock);
        verifyNoInteractions(serviceMock);
    }

    @Test
    @DisplayName("deve criar um item válido e redirecionar")
    void testSaveValid() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/save");
        when(requestMock.getParameter("title")).thenReturn("New Movie");
        when(requestMock.getParameter("mediaType")).thenReturn("MOVIE");
        
        servlet.doPost(requestMock, responseMock);
        
        verify(serviceMock).createItem(any(MediaItem.class));
        verify(responseMock).sendRedirect(anyString());
    }

    @Test
    @DisplayName("deve preservar rating e comment no update")
    void testUpdatePreservesRatingAndComment() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/update");
        when(requestMock.getParameter("id")).thenReturn("1");
        when(requestMock.getParameter("title")).thenReturn("Updated Title");
        when(requestMock.getParameter("mediaType")).thenReturn("MOVIE");
        
        MediaItem existing = new MediaItem();
        existing.setId(1);
        existing.setRating(5);
        existing.setComment("Excellent");
        when(serviceMock.getItemById(1)).thenReturn(existing);
        
        servlet.doPost(requestMock, responseMock);
        
        verify(serviceMock).updateItem(argThat(item -> item.getRating() != null && item.getRating() == 5 && "Excellent".equals(item.getComment())));
        verify(responseMock).sendRedirect(anyString());
    }

    @Test
    @DisplayName("deve retornar 400 ao atualizar item sem id")
    void testUpdateWithoutId() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/update");
        when(requestMock.getParameter("id")).thenReturn(null);
        when(requestMock.getParameter("title")).thenReturn("Updated Title");
        when(requestMock.getParameter("mediaType")).thenReturn("MOVIE");
        
        servlet.doPost(requestMock, responseMock);
        
        verify(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST);
        verify(serviceMock, never()).updateItem(any());
    }

    @Test
    @DisplayName("deve tratar erro de POST sem mediaType e nao retornar 500")
    void testPostWithoutMediaType() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/save");
        when(requestMock.getParameter("title")).thenReturn("No Type");
        when(requestMock.getParameter("mediaType")).thenReturn(""); // Missing or empty
        when(requestMock.getRequestDispatcher(anyString())).thenReturn(requestDispatcherMock);
        
        servlet.doPost(requestMock, responseMock);
        
        verify(requestMock).setAttribute(eq("errorKey"), anyString());
        verify(requestMock).setAttribute(eq("item"), any(MediaItem.class));
        verify(requestDispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    @DisplayName("deve tratar rate com nota invalida e nao cair no form novo")
    void testRateInvalidRating() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/rate");
        when(requestMock.getParameter("id")).thenReturn("1");
        when(requestMock.getParameter("rating")).thenReturn("invalid");
        when(requestMock.getRequestDispatcher(anyString())).thenReturn(requestDispatcherMock);
        
        MediaItem existing = new MediaItem();
        existing.setId(1);
        when(serviceMock.getItemById(1)).thenReturn(existing);
        
        servlet.doPost(requestMock, responseMock);
        
        verify(requestMock).setAttribute(eq("errorKey"), anyString());
        verify(requestDispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    @DisplayName("deve retornar 404 ao deletar id inexistente")
    void testDeleteNonExistent() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/delete");
        when(requestMock.getParameter("id")).thenReturn("999");
        doThrow(new ServiceException("Not found", null)).when(serviceMock).deleteItem(999);
        when(requestMock.getRequestDispatcher(anyString())).thenReturn(requestDispatcherMock);
        
        servlet.doPost(requestMock, responseMock);
        
        verify(requestMock).setAttribute(eq("errorKey"), anyString());
        verify(requestDispatcherMock).forward(requestMock, responseMock);
    }

    @Test
    @DisplayName("deve retornar [] para tmdb-search sem termo")
    void testTmdbSearchEmpty() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/tmdb-search");
        when(requestMock.getParameter("term")).thenReturn("");
        java.io.PrintWriter pw = mock(java.io.PrintWriter.class);
        when(responseMock.getWriter()).thenReturn(pw);
        
        servlet.doGet(requestMock, responseMock);
        
        verify(pw).write("[]");
    }

    @Test
    @DisplayName("deve retornar resultados em json para tmdb-search com termo e idioma")
    void testTmdbSearchWithResultsAndLanguage() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/tmdb-search");
        when(requestMock.getParameter("term")).thenReturn("Inception");
        when(requestMock.getParameter("lang")).thenReturn("en");
        
        MediaItem movie = new MediaItem();
        movie.setTitle("Inception");
        movie.setMediaType(MediaType.MOVIE);
        movie.setReleaseYear(2010);
        movie.setSynopsis("A thief who steals corporate secrets...");
        when(metadataProviderMock.searchByTitle("Inception", "en")).thenReturn(List.of(movie));

        java.io.PrintWriter pw = mock(java.io.PrintWriter.class);
        when(responseMock.getWriter()).thenReturn(pw);

        servlet.doGet(requestMock, responseMock);

        verify(metadataProviderMock).searchByTitle("Inception", "en");
        verify(pw).write(argThat((String s) -> s.contains("Inception") && s.contains("2010") && s.contains("MOVIE")));
    }

    @Test
    @DisplayName("deve retornar série em json para tmdb-search com tipo SERIES")
    void testTmdbSearchWithSeriesResult() throws Exception {
        when(requestMock.getPathInfo()).thenReturn("/tmdb-search");
        when(requestMock.getParameter("term")).thenReturn("Breaking Bad");
        when(requestMock.getParameter("lang")).thenReturn("pt-BR");
        
        MediaItem series = new MediaItem();
        series.setTitle("Breaking Bad");
        series.setMediaType(MediaType.SERIES);
        series.setReleaseYear(2008);
        series.setAuthorDirector("Vince Gilligan");
        when(metadataProviderMock.searchByTitle("Breaking Bad", "pt-BR")).thenReturn(List.of(series));

        java.io.PrintWriter pw = mock(java.io.PrintWriter.class);
        when(responseMock.getWriter()).thenReturn(pw);

        servlet.doGet(requestMock, responseMock);

        verify(metadataProviderMock).searchByTitle("Breaking Bad", "pt-BR");
        verify(pw).write(argThat((String s) -> s.contains("Breaking Bad") && s.contains("SERIES") && s.contains("Vince Gilligan")));
    }
}
