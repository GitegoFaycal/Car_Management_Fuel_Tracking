package org;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class CarCliApplication {

    private static final String BASE_URL = "http://localhost:8081/api/cars";

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("No command provided");
            return;
        }

        String command = args[0];
        Map<String, String> params = parseArgs(args);

        HttpClient client = HttpClient.newHttpClient();

        switch (command) {

            case "create-car" -> {
                String json = """
                        {
                          "brand": "%s",
                          "model": "%s",
                          "year": %s
                        }
                        """.formatted(
                        params.get("brand"),
                        params.get("model"),
                        params.get("year")
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                handleResponse(response);
            }

            case "add-fuel" -> {
                String carId = params.get("carId");

                String json = """
                        {
                          "liters": %s,
                          "price": %s,
                          "odometer": %s
                        }
                        """.formatted(
                        params.get("liters"),
                        params.get("price"),
                        params.get("odometer")
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/" + carId + "/fuel"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                handleResponse(response);
            }

            case "fuel-stats" -> {
                String carId = params.get("carId");

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/" + carId + "/fuel/stats"))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                handleResponse(response);
            }

            default -> System.out.println("Unknown command");
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < args.length - 1; i += 2) {
            map.put(args[i].replace("--", ""), args[i + 1]);
        }
        return map;
    }

    private static void handleResponse(HttpResponse<String> response) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println(response.body());
        } else {
            System.out.println("Error: HTTP " + response.statusCode());
            System.out.println(response.body());
        }
    }
}
