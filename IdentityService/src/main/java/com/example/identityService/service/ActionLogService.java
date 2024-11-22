package com.example.identityService.service;

import com.example.identityService.DTO.response.LoggerResponseDTO;
import com.example.identityService.DTO.response.PageResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ActionLogService {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public PageResponseDTO<LoggerResponseDTO> callProcedure(int page, int size, String query, String sortedBy, String sortDirection) {
        // Tạo query để gọi procedure
        StoredProcedureQuery spQuery = entityManager.createNamedStoredProcedureQuery("sp_get_logs");

        // Đăng ký các tham số vào
        spQuery.setParameter("p_page", page);
        spQuery.setParameter("p_size", size);
        spQuery.setParameter("p_query", query);
        spQuery.setParameter("p_sorted_by", sortedBy);
        spQuery.setParameter("p_sort_direction", sortDirection);

        // Thực thi query
        spQuery.execute();

        // Lấy kết quả tổng số bản ghi
        Integer totalRecords = (Integer) spQuery.getOutputParameterValue("total_records");

        // Lấy kết quả từ REF_CURSOR
        List<Object[]> results = spQuery.getResultList();

        List<LoggerResponseDTO> res = new ArrayList<>();

        for(Object[] item : results){
            res.add(LoggerResponseDTO.builder()
                            .id(item[0].toString())
                            .email(item[1].toString())
                            .ip(item[2].toString())
                            .actionName(item[3].toString())
                            .dateTime(item[4].toString())
                            .note(item[5] == null ? null : item[5].toString())
                    .build());
        }

        return PageResponseDTO.<LoggerResponseDTO>builder()
                .page(page)
                .size(size)
                .isLast(Objects.equals(page, totalRecords))
                .isFirst(Objects.equals(page, 1))
                .query(query)
                .sortedBy(sortedBy)
                .sortDirection(sortDirection)
                .totalRecords(totalRecords)
                .totalPages((int) Math.ceil((double) totalRecords / size))
                .response(res)
                .build();
    }
}
