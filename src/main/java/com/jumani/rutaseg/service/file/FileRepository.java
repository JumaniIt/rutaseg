package com.jumani.rutaseg.service.file;

import com.jumani.rutaseg.dto.result.Error;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FileRepository {
    Optional<String> findLinkToFile(String key);

    Optional<Error> save(String key, MultipartFile file);
}
