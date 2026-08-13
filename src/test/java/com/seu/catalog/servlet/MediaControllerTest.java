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
        items.add(new MediaItem("Test Movie", MediaType.MOVIE));
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
}
