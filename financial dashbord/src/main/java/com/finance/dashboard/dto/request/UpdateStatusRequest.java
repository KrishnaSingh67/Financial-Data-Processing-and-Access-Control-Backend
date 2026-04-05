package com.finance.dashboard.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "isActive field is required")
    private Boolean isActive;
}
