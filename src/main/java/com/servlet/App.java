package com.servlet;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Application Runner (Embedded Web Server)
 * 
 * Allows you to run this project directly in VS Code by clicking "Run".
 * Serves the student.jsp form and processes the /hello form submission.
 */
public class App {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // Create and start built-in HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Route 1: Root / student.jsp form
            server.createContext("/", new FormHandler());
            server.createContext("/student.jsp", new FormHandler());

            // Route 2: /hello POST handler (simulates HelloServlet -> result.jsp)
            server.createContext("/hello", new HelloServletHandler());

            server.setExecutor(null); // default executor
            server.start();

            System.out.println("==================================================================");
            System.out.println("  🚀 JSP & Servlet Application Started Successfully!");
            System.out.println("==================================================================");
            System.out.println("  📍 Server URL : http://localhost:" + PORT + "/");
            System.out.println("  📄 Form View  : http://localhost:" + PORT + "/student.jsp");
            System.out.println("  ⚙️ Controller : http://localhost:" + PORT + "/hello");
            System.out.println("==================================================================");
            System.out.println("  👉 Open http://localhost:" + PORT + "/ in your browser to view output.");
            System.out.println("  👉 Press Ctrl+C in terminal to stop server.");
            System.out.println("==================================================================\n");

            // Attempt to automatically open default browser
            try {
                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:" + PORT + "/"));
                }
            } catch (Exception ignored) {
                // Headless environment fallback
            }

        } catch (IOException e) {
            System.err.println("❌ Error starting server on port " + PORT + ": " + e.getMessage());
        }
    }

    /**
     * Serves the student.jsp registration form.
     */
    static class FormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Path jspPath = Paths.get("src/main/webapp/student.jsp");
            String htmlContent;

            if (Files.exists(jspPath)) {
                htmlContent = Files.readString(jspPath);
                // Strip JSP directives for pure HTML rendering
                htmlContent = htmlContent.replaceAll("<%@[^>]*%>", "");
            } else {
                htmlContent = getFallbackFormHtml();
            }

            byte[] responseBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    /**
     * Handles /hello submissions (Simulating HelloServlet and rendering result.jsp)
     */
    static class HelloServletHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Redirect GET to student.jsp
                exchange.getResponseHeaders().set("Location", "/student.jsp");
                exchange.sendResponseHeaders(302, -1);
                return;
            }

            // Read POST body
            InputStream is = exchange.getRequestBody();
            String formData = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = parseFormData(formData);

            String name = params.getOrDefault("name", "Guest Student");
            String email = params.getOrDefault("email", "Not provided");
            String course = params.getOrDefault("course", "Java & JSP Fundamentals");
            String age = params.getOrDefault("age", "N/A");

            // Console log
            System.out.println("📥 [HelloServlet] Form Received:");
            System.out.println("   - Name   : " + name);
            System.out.println("   - Email  : " + email);
            System.out.println("   - Course : " + course);
            System.out.println("   - Age    : " + age);
            System.out.println("   -> Forwarding to result.jsp\n");

            // Load and render result.jsp
            Path resultPath = Paths.get("src/main/webapp/result.jsp");
            String resultHtml;

            if (Files.exists(resultPath)) {
                resultHtml = Files.readString(resultPath);
                resultHtml = resultHtml.replaceAll("<%@[^>]*%>", "");
                // Replace JSP Expression Language (${name}, etc.)
                resultHtml = resultHtml.replace("${name}", name)
                                       .replace("${email}", email)
                                       .replace("${course}", course)
                                       .replace("${age}", age);
            } else {
                resultHtml = getFallbackResultHtml(name, email, course, age);
            }

            byte[] responseBytes = resultHtml.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                map.put(key, value);
            }
        }
        return map;
    }

    private static String getFallbackFormHtml() {
        return "<!DOCTYPE html><html><body><h2>Student Registration Form</h2>" +
               "<form action=\"/hello\" method=\"post\">" +
               "Name: <input type=\"text\" name=\"name\" required><br><br>" +
               "Email: <input type=\"email\" name=\"email\" required><br><br>" +
               "Course: <input type=\"text\" name=\"course\" required><br><br>" +
               "Age: <input type=\"number\" name=\"age\" required><br><br>" +
               "<input type=\"submit\" value=\"Register\">" +
               "</form></body></html>";
    }

    private static String getFallbackResultHtml(String name, String email, String course, String age) {
        return "<!DOCTYPE html><html><body><h2>Registration Result</h2>" +
               "<p><b>Name:</b> " + name + "</p>" +
               "<p><b>Email:</b> " + email + "</p>" +
               "<p><b>Course:</b> " + course + "</p>" +
               "<p><b>Age:</b> " + age + "</p>" +
               "<br><a href=\"/student.jsp\">← Back to Form</a>" +
               "</body></html>";
    }
}
