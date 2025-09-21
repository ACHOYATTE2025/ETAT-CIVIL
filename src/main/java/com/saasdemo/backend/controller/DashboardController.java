package com.saasdemo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.DashboardResponse;
import com.saasdemo.backend.service.AuthService;
import com.saasdemo.backend.service.DashboardService;
import com.saasdemo.backend.service.UtilisateurService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// This controller handles dashboard-related endpoints for the ETAT CIVIL application.
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(
  name = "DASHBOARD_CONTROLLER   REST Api for ETAT CIVIL",
  description="DASHBOARD_CONTROLLER  REST Api in ETAT CIVIL APP TO SHOW STATS"
)
public class DashboardController {

  // Injecting required services for user management, authentication, and dashboard statistics
  private final UtilisateurService utilisateurService;
  private final AuthService authService;
  private final DashboardService dashboardService;

  /*==========================================================================================
      Set up the dashboard endpoint
    ==========================================================================================*/
  /**
   * GET endpoint to retrieve the dashboard statistics.
   * @return DashboardResponse containing statistics data.
   */
  @GetMapping(path="/dashboard")
  public DashboardResponse dashboard() {
      log.info("➡️ Request received: GET /dashboard");
      DashboardResponse response = this.dashboardService.dash();
      log.info("✅ Dashboard statistics fetched successfully: {}", response);
      return response;
  }
}
