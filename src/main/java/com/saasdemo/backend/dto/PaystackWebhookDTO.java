package com.saasdemo.backend.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class PaystackWebhookDTO {

   private String event;
    private PaystackData data;

    @Data
    public static class PaystackData {
        private String id;
        private String status;
        private String reference;
        private BigDecimal amount;
        private String currency;
        private String paid_at;
        private Map<String, Object> metadata;
    }


    
}