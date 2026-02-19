package com.artnest.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String code;
    private String message;
    private Map<String, ?> details;
}
