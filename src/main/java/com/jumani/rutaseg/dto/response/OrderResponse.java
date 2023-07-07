package com.jumani.rutaseg.controller;

import com.amazonaws.util.StringUtils;
import com.jumani.rutaseg.domain.*;
import com.jumani.rutaseg.dto.request.OrderRequest;
import com.jumani.rutaseg.dto.request.TestRequest;
import com.jumani.rutaseg.dto.response.TestResponse;
import com.jumani.rutaseg.dto.response.SessionInfo;
import com.jumani.rutaseg.dto.result.Result;
import com.jumani.rutaseg.exception.ForbiddenException;
import com.jumani.rutaseg.exception.InvalidRequestException;
import com.jumani.rutaseg.exception.NotFoundException;
import com.jumani.rutaseg.handler.Session;
import com.jumani.rutaseg.repository.TestRepository;
import com.jumani.rutaseg.repository.file.FileRepository;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import com.jumani.rutaseg.handler.Session;

@AllArgsConstructor
@Getter
public class OrderResponse {

    private Long id;
    private boolean pema;
    private boolean port;
    private boolean transport;
    private OrderStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime finishedAt;
    private ArrivalData arrivalData;
    private DriverData driverData;
    private CustomsData customsData;

}

