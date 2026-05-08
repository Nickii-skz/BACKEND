package com.pos.infrastructure.adapter.input.dto.response;

import java.util.List;

/**
 * Generic paged response wrapper.
 */
public record PagedResponse<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) {
    public static <T> PagedResponse<T> of(List<T> content, long totalElements, int totalPages, int currentPage, int pageSize) {
        return new PagedResponse<>(content, totalElements, totalPages, currentPage, pageSize);
    }
}
