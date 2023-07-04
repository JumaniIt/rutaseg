package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Client extends User{
    private String phone;
    private List<Consignee> consignees;

    public Client(String phone) {
        this.phone = phone;
        this.consignees = new ArrayList<>();
    }

    public Client() {
        this.consignees = new ArrayList<>();
    }

    public Optional<Error> addConsignee(Consignee consignee) {
        String consigneeName = consignee.getName();
        long consigneeCUIT = consignee.getCUIT();

        for (Consignee existingConsignee : consignees) {
            if (existingConsignee.getName().equals(consigneeName) || existingConsignee.getCUIT() == consigneeCUIT) {
                return Optional.of(new Error("duplicate_consignee", "Consignee with the same name or CUIT already exists."));
            }
        }

        consignees.add(consignee);
        return Optional.empty();
    }
}
