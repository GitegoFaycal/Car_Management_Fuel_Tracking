package org.backend.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backend.service.CarService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class FuelStatsServlet extends HttpServlet {

    private CarService carService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        this.carService = context.getBean(CarService.class);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles GET requests like:
     * GET /servlet/fuel-stats?carId=1
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String carIdParam = request.getParameter("carId");

        if (carIdParam == null || carIdParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Missing required parameter: carId\"}");
            out.flush();
            return;
        }

        long carId;
        try {
            carId = Long.parseLong(carIdParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid carId format. Must be a number.\"}");
            out.flush();
            return;
        }

        try {
            // Use the same service method as the controller
            Map<String, Double> stats = carService.getFuelStats(carId);

            if (stats == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Car not found with id: " + carId + "\"}");
                out.flush();
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            String jsonResponse = objectMapper.writeValueAsString(stats);
            out.print(jsonResponse);
            out.flush();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            out.flush();
        }
    }

    // Other HTTP methods are not allowed
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        methodNotAllowed(resp, "POST");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        methodNotAllowed(resp, "PUT");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        methodNotAllowed(resp, "DELETE");
    }

    private void methodNotAllowed(HttpServletResponse response, String method) throws IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{\"error\":\"Method " + method + " not allowed. Use GET.\"}");
        out.flush();
    }
}
