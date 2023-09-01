package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
@Getter
@Entity
@Table(name = "documents")
@Slf4j
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;


    @Column(name = "name")
    private String name;

    @Column(name = "resource")
    private String resource;

    public Document(String fileName, String key) {
    }
    public Document(long id, ZonedDateTime createdAt, String name, String resource) {
        this.id = id;
        this.createdAt = createdAt;
        this.name = name;
        this.resource = resource;
    }

    public Document() {
    }
}
