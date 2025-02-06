package com.polymath.jobboard.dto.response;

import org.springframework.data.domain.Pageable;

import java.util.List;

public record PaginationResponse(Pageable pageable, List<?> data) {
}
