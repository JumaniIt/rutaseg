package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.Consignee;

import java.util.List;

public record ClientResponse(long id, Long userId, String phone, Long CUIT, List<Consignee> consignees) {
}