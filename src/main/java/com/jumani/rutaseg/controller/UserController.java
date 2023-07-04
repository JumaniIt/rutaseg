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
@RequestMapping("/users")
public class UserController {


}
