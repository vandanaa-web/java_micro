import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * WebServer implements an HTTP server using standard library com.sun.net.httpserver.HttpServer.
 * Serves static web assets from the web/ directory and provides RESTful JSON API endpoints
 * for command execution, state retrieval, and game restarts.
 */
public class WebServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private GameEngine engine;

    /**
     * Constructor for WebServer. Instantiates GameEngine and configures HTTP contexts.
     */
    public WebServer() {
        this.engine = new GameEngine();
    }

    /**
     * Starts the HTTP server on port 8080.
     */
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Register route handlers
            server.createContext("/api/command", new CommandHandler());
            server.createContext("/api/restart", new RestartHandler());
            server.createContext("/api/state", new StateHandler());
            server.createContext("/", new StaticFileHandler());

            server.setExecutor(null); // Default executor
            server.start();

            System.out.println("Server running at http://localhost:" + PORT);
        } catch (IOException e) {
            System.err.println("Failed to start HTTP server: " + e.getMessage());
        }
    }

    /**
     * Helper method to construct JSON response string by hand with string escaping.
     *
     * @param outputText Main text to display.
     * @return JSON string representation.
     */
    private synchronized String buildJsonState(String outputText) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"output\": \"").append(escapeJson(outputText)).append("\",\n");
        sb.append("  \"room\": \"").append(escapeJson(engine.getPlayer().getCurrentRoom().getName())).append("\",\n");
        sb.append("  \"movesLeft\": ").append(engine.getMovesLeft()).append(",\n");
        sb.append("  \"inventory\": [");

        String invStr = engine.getPlayer().getInventoryString();
        if (!invStr.equals("nothing") && !invStr.isEmpty()) {
            String[] items = invStr.split(",\\s*");
            for (int i = 0; i < items.length; i++) {
                sb.append("\"").append(escapeJson(items[i])).append("\"");
                if (i < items.length - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("],\n");
        sb.append("  \"gameOver\": ").append(engine.isGameOver()).append(",\n");
        sb.append("  \"won\": ").append(engine.hasWon()).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Escapes special characters in string for valid JSON output.
     *
     * @param s Input string.
     * @return JSON-escaped string.
     */
    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Handler for POST /api/command endpoint.
     */
    private class CommandHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }

            InputStream is = exchange.getRequestBody();
            String command = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();

            String responseText = engine.processCommand(command);
            String jsonResponse = buildJsonState(responseText);

            sendResponse(exchange, 200, "application/json; charset=UTF-8", jsonResponse);
        }
    }

    /**
     * Handler for POST /api/restart endpoint.
     */
    private class RestartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }

            synchronized (WebServer.this) {
                engine = new GameEngine();
            }

            String welcomeText = engine.getWelcomeText();
            String jsonResponse = buildJsonState(welcomeText);

            sendResponse(exchange, 200, "application/json; charset=UTF-8", jsonResponse);
        }
    }

    /**
     * Handler for GET /api/state endpoint.
     */
    private class StateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }

            String stateText = engine.getWelcomeText();
            String jsonResponse = buildJsonState(stateText);

            sendResponse(exchange, 200, "application/json; charset=UTF-8", jsonResponse);
        }
    }

    /**
     * Handler for serving static HTML, CSS, and JS files from web/ folder.
     */
    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("web", path);
            if (!file.exists() || !file.isFile()) {
                sendResponse(exchange, 404, "text/plain", "404 Not Found");
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) {
                contentType = "text/html; charset=UTF-8";
            } else if (path.endsWith(".css")) {
                contentType = "text/css; charset=UTF-8";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript; charset=UTF-8";
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            sendResponseBytes(exchange, 200, contentType, bytes);
        }
    }

    /**
     * Helper to send string response back over HttpExchange.
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        sendResponseBytes(exchange, statusCode, contentType, bytes);
    }

    /**
     * Helper to send byte array response back over HttpExchange.
     */
    private void sendResponseBytes(HttpExchange exchange, int statusCode, String contentType, byte[] bytes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
