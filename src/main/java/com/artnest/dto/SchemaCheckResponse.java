package com.artnest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchemaCheckResponse {
    private Boolean bookingSlotSchemaReady;
    private List<String> missingTables;
    private List<String> missingColumns;
}
