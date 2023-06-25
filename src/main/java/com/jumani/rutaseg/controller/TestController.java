package com.jumani.rutaseg.controller;

import com.amazonaws.util.StringUtils;
import com.jumani.rutaseg.domain.TestEntity;
import com.jumani.rutaseg.dto.request.TestRequest;
import com.jumani.rutaseg.dto.response.TestResponse;
import com.jumani.rutaseg.dto.response.UserSessionInfo;
import com.jumani.rutaseg.exception.InvalidRequestException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.exception.ValidationException;
import com.jumani.rutaseg.handler.USI;
import com.jumani.rutaseg.repository.TestRepository;
import com.jumani.rutaseg.repository.file.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
// Transactional permite hacer transacciones de escritura a la DB
@Transactional
@RequestMapping("/test")
public class TestController {

    private final TestRepository repo;

    private final FileRepository fileRepository;

    public TestController(TestRepository repo,
                          FileRepository fileRepository) {
        this.repo = repo;
        this.fileRepository = fileRepository;
    }

    @PostMapping
    public ResponseEntity<TestResponse> create(@RequestBody TestRequest request, @USI UserSessionInfo usi) {
        this.validateCreationRequest(request);

        final TestEntity testEntity = new TestEntity(request.getStringField(), request.getLongField(), request.getEnumField());

        final TestEntity savedEntity = repo.save(testEntity);

        final TestResponse response = this.createResponse(savedEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResponse> getById(@PathVariable("id") long id) {
        final TestEntity foundEntity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("test entity with id [%s] not found", id)));

        final TestResponse response = this.createResponse(foundEntity);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/file")
    public ResponseEntity<Boolean> saveFile(@RequestParam(value = "file") MultipartFile mf) {
        fileRepository.save(System.currentTimeMillis() + "-" + mf.getResource().getFilename(), mf);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/file/{key}")
    public ResponseEntity<String> getFile(@PathVariable("key") String key) {
        final String link = fileRepository.findLinkToFile(key)
                .orElseThrow(() -> new ValidationException("not_found", "file not found"));

        return ResponseEntity.ok(link);
    }

    private void validateCreationRequest(TestRequest request) {
        if (StringUtils.isNullOrEmpty(request.getStringField())) {
            throw new InvalidRequestException("string field cannot be null or empty");
        }

        if (Objects.isNull(request.getLongField()) || request.getLongField() <= 0) {
            throw new InvalidRequestException("long field must be a positive number");
        }

        if (Objects.isNull(request.getEnumField())) {
            throw new InvalidRequestException("enum field cannot be null");
        }
    }

    private TestResponse createResponse(TestEntity entity) {
        return new TestResponse(entity.getId(), entity.getStringField(), entity.getLongField(),
                entity.getEnumField(), entity.getDateField());
    }

}
