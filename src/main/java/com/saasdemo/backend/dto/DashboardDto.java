package com.saasdemo.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDto {

  private Long nombreAdmin;
  private Long nombreUser;
  private Long nombreAdminActive;
  private Long nombreAdminDesactive;
  private Long nombreUserActive;
  private Long nombreUserDesactive;


    
}