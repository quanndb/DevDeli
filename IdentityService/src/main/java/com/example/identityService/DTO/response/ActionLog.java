package com.example.identityService.DTO.response;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@NamedStoredProcedureQuery(
        name = "sp_get_logs",  // Tên query để gọi
        procedureName = "sp_get_logs",  // Tên procedure trong PostgreSQL
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_page", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_size", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_query", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sorted_by", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sort_direction", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "total_records", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "result_cursor", type = void.class)
        }
)
public class ActionLog {
    @Id
    private String id;
    private String email;
    private String ip;
    private String actionName;
    private LocalDateTime createdDate;
    private String note;
}
