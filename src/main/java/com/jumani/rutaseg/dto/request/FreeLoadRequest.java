package com.jumani.rutaseg.dto.request;

import com.jumani.rutaseg.domain.FreeLoadType;
import com.jumani.rutaseg.domain.Measures;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FreeLoadRequest {

    private String patent;

    private FreeLoadType type;

    private String weight;

    private String guide;

    private String pema;

}
