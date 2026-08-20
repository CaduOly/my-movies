package com.seu.catalog.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.jstl.core.Config;

import java.io.IOException;
import java.util.Set;

/**
 * Servlet responsável por trocar o idioma (locale) da aplicação.
 */
@WebServlet("/lang")
public class LocaleController extends HttpServlet {
    private static final Set<String> SUPPORTED = Set.of("pt_BR", "en");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        if (code != null && SUPPORTED.contains(code)) {
            HttpSession session = req.getSession();
            Config.set(session, Config.FMT_LOCALE, code);
            session.setAttribute("appLocale", code.replace('_', '-'));
        }

        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            resp.sendRedirect(referer);
        } else {
            resp.sendRedirect(req.getContextPath() + "/app/home");
        }
    }
}
