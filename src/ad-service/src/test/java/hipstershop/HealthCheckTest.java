package hipstershop;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class HealthCheckTest {

    private Server server;
    private HealthCheckHandler healthCheckHandler;
    private static final int TEST_PORT = 9090;

    @BeforeEach
    void setUp() throws Exception {
        healthCheckHandler = new HealthCheckHandler();

        server = new Server(TEST_PORT);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(healthCheckHandler), "/health");

        server.start();

        RestAssured.port = TEST_PORT;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterEach
    void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    @DisplayName("Should return 200 and healthy status when service is healthy")
    void testHealthEndpointWhenHealthy() {
        healthCheckHandler.setHealthy(true);

        given()
            .when()
                .get("/health")
            .then()
                .statusCode(200)
                .contentType("application/json")
                .body("status", equalTo("healthy"))
                .body("service", equalTo("adservice"));
    }

    @Test
    @DisplayName("Should return 503 and unhealthy status when service is not healthy")
    void testHealthEndpointWhenUnhealthy() {
        healthCheckHandler.setHealthy(false);

        given()
            .when()
                .get("/health")
            .then()
                .statusCode(503)
                .contentType("application/json")
                .body("status", equalTo("unhealthy"))
                .body("service", equalTo("adservice"));
    }

    @Test
    @DisplayName("Should toggle health status correctly")
    void testHealthStatusToggle() {
        // Start healthy
        healthCheckHandler.setHealthy(true);
        assertTrue(healthCheckHandler.isHealthy());

        given()
            .when()
                .get("/health")
            .then()
                .statusCode(200)
                .body("status", equalTo("healthy"));

        // Change to unhealthy
        healthCheckHandler.setHealthy(false);
        assertFalse(healthCheckHandler.isHealthy());

        given()
            .when()
                .get("/health")
            .then()
                .statusCode(503)
                .body("status", equalTo("unhealthy"));

        // Change back to healthy
        healthCheckHandler.setHealthy(true);
        assertTrue(healthCheckHandler.isHealthy());

        given()
            .when()
                .get("/health")
            .then()
                .statusCode(200)
                .body("status", equalTo("healthy"));
    }

    @Test
    @DisplayName("Should have correct content type and charset")
    void testContentTypeAndCharset() {
        healthCheckHandler.setHealthy(true);

        given()
            .when()
                .get("/health")
            .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"))
                .header("Content-Type", containsStringIgnoringCase("utf-8"));
    }

    @Test
    @DisplayName("Health handler should start in healthy state by default")
    void testDefaultHealthyState() {
        HealthCheckHandler handler = new HealthCheckHandler();
        assertTrue(handler.isHealthy());
    }
}
