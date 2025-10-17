package com.saasdemo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.DashboardResponse;
import com.saasdemo.backend.service.AuthService;
import com.saasdemo.backend.service.DashboardService;
import com.saasdemo.backend.service.UtilisateurService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor


@Tag(
  name = "DASHBOARD_CONTROLLER   REST Api for ETAT CIVIL",
  description="DASHBOARD_CONTROLLER  REST Api in  ETAT CIVIL APP TO SHOW STATS "
)
public class DashboardController {

  private final UtilisateurService utilisateurService;
  private final AuthService authService;
  private final DashboardService dashboardService;



/*==================================================================================================*/
                        /*    mise ne place du tableau de bord */
/*================================================================================================= */
  @GetMapping(path="/dashboard")
  public DashboardResponse dashboard() {
      return this.dashboardService.dash();
  }
  
  
    
}