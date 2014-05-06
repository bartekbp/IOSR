package pl.edu.agh.kaflog.storm.httpServer;


import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pl.edu.agh.kaflog.storm.KaflogStorm;

public class StormHttpHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        String response = "Last log: " + KaflogStorm.getLastLog();
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}