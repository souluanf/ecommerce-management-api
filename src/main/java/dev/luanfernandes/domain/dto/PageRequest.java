package dev.luanfernandes.domain.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PageRequest(int pageNumber, int pageSize, LocalDate startDate, LocalDate endDate, String sort) {

    public static PageRequest of(int pageNumber, int pageSize) {
        return PageRequest.builder().pageNumber(pageNumber).pageSize(pageSize).build();
    }

    public static PageRequest of(int pageNumber, int pageSize, String sort) {
        return PageRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sort(sort)
                .build();
    }

    public static PageRequest of(int pageNumber, int pageSize, LocalDate startDate, LocalDate endDate) {
        return PageRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static PageRequest of(int pageNumber, int pageSize, LocalDate startDate, LocalDate endDate, String sort) {
        return PageRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .startDate(startDate)
                .endDate(endDate)
                .sort(sort)
                .build();
    }

    public int getOffset() {
        return pageNumber * pageSize;
    }

    public boolean hasDateFilter() {
        return startDate != null && endDate != null;
    }

    public boolean hasSort() {
        return sort != null && !sort.trim().isEmpty();
    }
}
