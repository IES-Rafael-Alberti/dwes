package edu.iesrafaelalberti.dwes.helloserver;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    String home() {
        return """
                <!doctype html>
                <html lang="es">
                  <head><meta charset="utf-8"><title>Hello Server</title></head>
                  <body><h1>Hola desde el servidor</h1></body>
                </html>
                """;
    }

    @GetMapping("/api/hello")
    Map<String, String> hello() {
        return Map.of("message", "Hola desde el servidor");
    }

    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
