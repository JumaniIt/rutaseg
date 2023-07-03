package com.jumani.rutaseg.domain;
import com.jumani.rutaseg.dto.result.Error;
import com.jumani.rutaseg.util.DateGen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

@Getter
public class Client {
    private String phone;
    private Consignee consignee;
    //constructor
    public Client(String phone, Consignee consignee) {
        this.phone = phone;
        this.consignee = consignee;
    }
    public Client() {
    }
    public void addConsignee(Consignee consignee) throws Exception {
        if (consignee == null) {
            throw new Exception("Error: Consignee object cannot be null.");
        }

        String consigneeName = consignee.getName();
        long consigneeCUIT = consignee.getCUIT();

        if (this.consignee != null) {
            if (this.consignee.getName().equals(consigneeName) || this.consignee.getCUIT() == consigneeCUIT) {
                throw new Exception("Error: Consignee with the same name or CUIT already exists.");
            }
        }
        this.consignee = consignee;
    }

    public Consignee getConsignee() {
        return consignee;
    }
}
