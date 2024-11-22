package com.example.identityService.DTO.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PageResponseDTO<T> {
    int page;
    int size;
    int totalPages;
    int totalRecords;
    boolean isLast;
    boolean isFirst;
    String query;
    String sortedBy;
    String sortDirection;
    List<T> response;
}
