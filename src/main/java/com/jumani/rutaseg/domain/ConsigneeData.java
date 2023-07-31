package com.jumani.rutaseg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@Getter
@Entity
@FieldNameConstants
@Table(name = "consinee_datas")
public class ConsigneeData {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;


        @Column(name = "name")
        private String name;

        @Column(name = "cuit")
        private Long cuit;

        public ConsigneeData(String name, Long cuit) {
            this.name = name;
            this.cuit = cuit;
        }

        public ConsigneeData() {
        }
    }



