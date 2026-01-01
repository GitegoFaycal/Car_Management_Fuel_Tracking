package org.backend.config;


import org.backend.servlet.FuelStatsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to register the manual servlet with Spring Boot.
 * This allows the servlet to be accessible at /servlet/fuel-stats
 */
@Configuration
public class ServletConfig {

    /**
     * Register the FuelStatsServlet at the path /servlet/fuel-stats
     *
     * This makes the servlet accessible at:
     * GET <a href="http://localhost:8081/servlet/fuel-stats?carId=1">...</a>
     */
    @Bean
    public ServletRegistrationBean<FuelStatsServlet> fuelStatsServlet() {
        ServletRegistrationBean<FuelStatsServlet> registrationBean =
                new ServletRegistrationBean<>(new FuelStatsServlet(), "/servlet/fuel-stats");

        // Optional: Set load-on-startup order
        registrationBean.setLoadOnStartup(1);

        return registrationBean;
    }
}