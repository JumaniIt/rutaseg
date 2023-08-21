package com.jumani.rutaseg.dto.result;

import java.util.List;

public record PaginatedResult<T>(long totalElements, int totalPages, int pageSize, int page, List<T> elements) {

}
