package com.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller Layer Servlet
 * URL Mapping: /hello
 */
@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the student registration form
        response.sendRedirect(request.getContextPath() + "/student.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Ensure proper character encoding for incoming form data
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 1. Read input parameters from form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String course = request.getParameter("course");
        String ageStr = request.getParameter("age");

        // Fallback defaults for missing/empty fields
        if (name == null || name.trim().isEmpty()) {
            name = "Guest Student";
        }
        if (email == null || email.trim().isEmpty()) {
            email = "Not provided";
        }
        if (course == null || course.trim().isEmpty()) {
            course = "Java & JSP Fundamentals";
        }
        if (ageStr == null || ageStr.trim().isEmpty()) {
            ageStr = "N/A";
        }

        // 2. Set attributes in request scope for result.jsp
        request.setAttribute("name", name.trim());
        request.setAttribute("email", email.trim());
        request.setAttribute("course", course.trim());
        request.setAttribute("age", ageStr.trim());

        // 3. Forward request to result.jsp
        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }
}
