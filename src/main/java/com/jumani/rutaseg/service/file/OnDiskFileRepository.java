package com.jumani.rutaseg.service.file;

import com.jumani.rutaseg.dto.result.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Profile("local | integration_tests")
public class OnDiskFileRepository implements FileRepository {

    @Value("${files.directory}")
    private String path;

    @Override
    public Optional<String> findLinkToFile(String key) {
        FileInputStream fileInputStream = null;
        try {
            final String pathToFile = path + "/" + key;
            fileInputStream = new FileInputStream(pathToFile);
            return Optional.of(pathToFile);
        } catch (FileNotFoundException e) {
            return Optional.empty();

        } finally {
            try {
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Optional<Error> save(String key, MultipartFile mf) {
        final File file = new File(path + "/" + key);
        try {
            mf.transferTo(file);
            return Optional.empty();
        } catch (IOException e) {
            log.error("could not save file to disk", e);
            return Optional.of(new Error("save_file_to_disk_failed", e.getMessage()));
        }
    }
}
