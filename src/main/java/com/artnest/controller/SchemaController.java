package com.artnest.controller;

import com.artnest.dto.ApiResponse;
import com.artnest.dto.SchemaCheckResponse;
import com.artnest.service.SchemaInspectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/schema")
public class SchemaController {

    private final SchemaInspectionService schemaInspectionService;

    public SchemaController(SchemaInspectionService schemaInspectionService) {
        this.schemaInspectionService = schemaInspectionService;
    }

    @GetMapping("/booking-slot")
    public ResponseEntity<ApiResponse<SchemaCheckResponse>> checkBookingSlotSchema() {
        SchemaCheckResponse response = schemaInspectionService.checkBookingSlotSchema();
        String message = response.getBookingSlotSchemaReady()
                ? "Booking slot schema is ready"
                : "Booking slot schema has missing tables/columns";
        return ResponseEntity.ok(new ApiResponse<>(true, message, response));
    }
}
