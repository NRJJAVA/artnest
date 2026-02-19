package com.artnest.dto;

import com.artnest.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDefaultModeRequest {
    @NotNull(message = "defaultMode is required")
    private UserRole defaultMode;
}
