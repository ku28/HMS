package com.hotelmanagement.hotelmanagementbackend.common;

import org.springframework.data.domain.Page;

import java.util.List;

public final class PagedResponseMapper {

    private PagedResponseMapper() {
    }

    public static <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    public static <T, R> PagedResponse<R> toPagedResponse(Page<T> page, List<R> mappedContent) {
        return PagedResponse.<R>builder()
                .content(mappedContent)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }
}
