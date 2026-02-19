package com.artnest.service;

import com.artnest.dto.SchemaCheckResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchemaInspectionService {

    private final JdbcTemplate jdbcTemplate;

    public SchemaInspectionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SchemaCheckResponse checkBookingSlotSchema() {
        List<String> missingTables = new ArrayList<>();
        List<String> missingColumns = new ArrayList<>();

        ensureTable("bookings", missingTables);
        ensureTable("artist_availability", missingTables);
        ensureTable("artist_time_off", missingTables);

        ensureColumn("bookings", "booking_date", missingColumns);
        ensureColumn("bookings", "booking_end_date", missingColumns);
        ensureColumn("bookings", "duration_minutes", missingColumns);
        ensureColumn("bookings", "art_type", missingColumns);
        ensureColumn("bookings", "customer_id", missingColumns);
        ensureColumn("bookings", "artist_id", missingColumns);
        ensureColumn("bookings", "status", missingColumns);

        ensureColumn("artist_availability", "artist_id", missingColumns);
        ensureColumn("artist_availability", "day_of_week", missingColumns);
        ensureColumn("artist_availability", "start_time", missingColumns);
        ensureColumn("artist_availability", "end_time", missingColumns);
        ensureColumn("artist_availability", "slot_duration_minutes", missingColumns);
        ensureColumn("artist_availability", "is_active", missingColumns);

        ensureColumn("artist_time_off", "artist_id", missingColumns);
        ensureColumn("artist_time_off", "start_date_time", missingColumns);
        ensureColumn("artist_time_off", "end_date_time", missingColumns);

        boolean ready = missingTables.isEmpty() && missingColumns.isEmpty();
        return new SchemaCheckResponse(ready, missingTables, missingColumns);
    }

    private void ensureTable(String tableName, List<String> missingTables) {
        Integer count = jdbcTemplate.queryForObject(
                """
                select count(*) 
                from information_schema.tables 
                where table_schema = 'public' and table_name = ?
                """,
                Integer.class,
                tableName
        );
        if (count == null || count == 0) {
            missingTables.add(tableName);
        }
    }

    private void ensureColumn(String tableName, String columnName, List<String> missingColumns) {
        Integer count = jdbcTemplate.queryForObject(
                """
                select count(*) 
                from information_schema.columns 
                where table_schema = 'public' and table_name = ? and column_name = ?
                """,
                Integer.class,
                tableName,
                columnName
        );
        if (count == null || count == 0) {
            missingColumns.add(tableName + "." + columnName);
        }
    }
}
