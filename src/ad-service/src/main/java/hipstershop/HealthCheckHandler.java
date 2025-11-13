/*
 * Copyright 2018, Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hipstershop;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HealthCheckHandler extends HttpServlet {

  private static final Logger logger = LogManager.getLogger(HealthCheckHandler.class);
  private volatile boolean isHealthy = true;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");

    if (isHealthy) {
      resp.setStatus(HttpServletResponse.SC_OK);
      resp.getWriter().write("{\"status\":\"healthy\",\"service\":\"adservice\"}");
      logger.debug("Health check: OK");
    } else {
      resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      resp.getWriter().write("{\"status\":\"unhealthy\",\"service\":\"adservice\"}");
      logger.warn("Health check: UNHEALTHY");
    }
  }

  public void setHealthy(boolean healthy) {
    this.isHealthy = healthy;
  }

  public boolean isHealthy() {
    return this.isHealthy;
  }
}
