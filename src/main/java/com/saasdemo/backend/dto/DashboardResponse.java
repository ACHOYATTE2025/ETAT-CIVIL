package com.saasdemo.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

  private Long nombreAdmin;
  private Long nombreUser;
  private Long nombreAdminActive;
  private Long nombreAdminDesactive;
  private Long nombreUserActive;
  private Long nombreUserDesactive;
  private Long nombreCertificatMariage;
  private Long nombreCertificatDeces;
  private Long nombreExtraitNaissance;

}