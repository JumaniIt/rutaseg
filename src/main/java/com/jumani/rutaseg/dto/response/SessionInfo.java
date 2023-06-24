package com.jumani.rutaseg.dto.response;

import com.jumani.rutaseg.domain.TestEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfo {
    private String jwt;
    private TestEntity user; // TODO change type to User
}
