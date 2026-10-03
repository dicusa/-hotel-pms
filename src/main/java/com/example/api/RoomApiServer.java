package com.example.api;

import com.example.model.Room;
import com.example.repository.RoomRepository;
import com.example.security.JwtUtil;
import com.example.security.RoleCheckFilter;
import com.example.service.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class RoomApiServer {
    private HttpServer server;
    private final int port = 8080;
    private final EntityManagerFactory emf;
    private final AuthService authService;

    public RoomApiServer() {
        emf = Persistence.createEntityManagerFactory("hotelPU");
        authService = new AuthService(emf.createEntityManager());
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/rooms", new RoomsHandler());
        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
        if (emf != null) {
            emf.close();
        }
    }

    private class RoomsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            // /rooms or /rooms/{id}
            if (parts.length == 2) {
                // /rooms
                switch (method) {
                    case "GET":
                        handleGetAll(exchange);
                        break;
                    case "POST":
                        handleCreate(exchange);
                        break;
                    default:
                        sendResponse(exchange, 405, "Method Not Allowed");
                }
            } else if (parts.length == 3) {
                // /rooms/{id}
                Long id;
                try {
                    id = Long.parseLong(parts[2]);
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Invalid ID");
                    return;
                }
                switch (method) {
                    case "GET":
                        handleGetById(exchange, id);
                        break;
                    case "PUT":
                        handleUpdate(exchange, id);
                        break;
                    case "DELETE":
                        handleDelete(exchange, id);
                        break;
                    default:
                        sendResponse(exchange, 405, "Method Not Allowed");
                }
            } else {
                sendResponse(exchange, 404, "Not Found");
            }
        }

        private void handleGetAll(HttpExchange exchange) throws IOException {
            if (!isAuthorized(exchange, RoleCheckFilter.Role.ADMIN)) {
                return;
            }
            EntityManager em = emf.createEntityManager();
            RoomRepository repo = new RoomRepository(em);
            List<Room> rooms = repo.findAll();
            JSONArray arr = new JSONArray();
            for (Room r : rooms) {
                arr.put(roomToJson(r));
            }
            sendJson(exchange, 200, arr);
            em.close();
        }

        private void handleGetById(HttpExchange exchange, Long id) throws IOException {
            if (!isAuthorized(exchange, RoleCheckFilter.Role.ADMIN)) {
                return;
            }
            EntityManager em = emf.createEntityManager();
            RoomRepository repo = new RoomRepository(em);
            Optional<Room> opt = repo.findById(id);
            if (opt.isPresent()) {
                sendJson(exchange, 200, roomToJson(opt.get()));
            } else {
                sendResponse(exchange, 404, "Room not found");
            }
            em.close();
        }

        private void handleCreate(HttpExchange exchange) throws IOException {
            if (!isAuthorized(exchange, RoleCheckFilter.Role.ADMIN)) {
                return;
            }
            String body = readBody(exchange);
            JSONObject json;
            try {
                json = new JSONObject(body);
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON");
                return;
            }
            String number = json.optString("number", null);
            String type = json.optString("type", null);
            Double priceD = json.optDouble("price", -1);
            if (number == null || type == null || priceD <= 0) {
                sendResponse(exchange, 400, "Missing or invalid fields");
                return;
            }
            EntityManager em = emf.createEntityManager();
            RoomRepository repo = new RoomRepository(em);
            if (repo.findByNumber(number).isPresent()) {
                sendResponse(exchange, 400, "Room number already exists");
                em.close();
                return;
            }
            Room room = new Room(number, type, java.math.BigDecimal.valueOf(priceD));
            repo.save(room);
            sendJson(exchange, 201, roomToJson(room));
            em.close();
        }

        private void handleUpdate(HttpExchange exchange, Long id) throws IOException {
            if (!isAuthorized(exchange, RoleCheckFilter.Role.ADMIN)) {
                return;
            }
            String body = readBody(exchange);
            JSONObject json;
            try {
                json = new JSONObject(body);
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON");
                return;
            }
            EntityManager em = emf.createEntityManager();
            RoomRepository repo = new RoomRepository(em);
            Optional<Room> opt = repo.findById(id);
            if (!opt.isPresent()) {
                sendResponse(exchange, 404, "Room not found");
                em.close();
                return;
            }
            Room room = opt.get();
            String number = json.optString("number", null);
            String type = json.optString("type", null);
            Double priceD = json.optDouble("price", -1);
            if (number != null && !number.equals(room.getNumber())) {
                if (repo.findByNumber(number).isPresent()) {
                    sendResponse(exchange, 400, "Room number already exists");
                    em.close();
                    return;
                }
                room.setNumber(number);
            }
            if (type != null) room.setType(type);
            if (priceD > 0) room.setPrice(java.math.BigDecimal.valueOf(priceD));
            repo.save(room);
            sendJson(exchange, 200, roomToJson(room));
            em.close();
        }

        private void handleDelete(HttpExchange exchange, Long id) throws IOException {
            if (!isAuthorized(exchange, RoleCheckFilter.Role.ADMIN)) {
                return;
            }
            EntityManager em = emf.createEntityManager();
            RoomRepository repo = new RoomRepository(em);
            Optional<Room> opt = repo.findById(id);
            if (!opt.isPresent()) {
                sendResponse(exchange, 404, "Room not found");
                em.close();
                return;
            }
            repo.delete(opt.get());
            sendResponse(exchange, 204, "");
            em.close();
        }

        private boolean isAuthorized(HttpExchange exchange, RoleCheckFilter.Role required) throws IOException {
            List<String> authHeaders = exchange.getRequestHeaders().get("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                sendResponse(exchange, 401, "Missing Authorization header");
                return false;
            }
            String auth = authHeaders.get(0);
            if (!auth.startsWith("Bearer ")) {
                sendResponse(exchange, 401, "Invalid Authorization header");
                return false;
            }
            String token = auth.substring(7);
            if (!RoleCheckFilter.hasRole(token, required)) {
                sendResponse(exchange, 403, "Forbidden");
                return false;
            }
            return true;
        }

        private String readBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }

        private void sendResponse(HttpExchange exchange, int status, String msg) throws IOException {
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private void sendJson(HttpExchange exchange, int status, Object jsonObj) throws IOException {
            byte[] bytes = jsonObj.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

        private JSONObject roomToJson(Room r) {
            JSONObject obj = new JSONObject();
            obj.put("id", r.getId());
            obj.put("number", r.getNumber());
            obj.put("type", r.getType());
            obj.put("price", r.getPrice());
            return obj;
        }
    }
}
