package com.jumani.rutaseg.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;

@EqualsAndHashCode(exclude = "createdAt")
@AllArgsConstructor
@Getter
public class DocumentResponse {
    private Long id;
    private ZonedDateTime createdAt;
    private String name;
    private String resource;


}
