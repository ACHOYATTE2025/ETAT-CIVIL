package com.saasdemo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasdemo.backend.dto.DashboardResponse;
import com.saasdemo.backend.service.AuthService;
import com.saasdemo.backend.service.DashboardService;
import com.saasdemo.backend.service.UtilisateurService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
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