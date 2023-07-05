package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class Client {
    private User user;
    private String phone;
    private Long CUIT;
    private List<Consignee> consignees;

    public Client(User user, String phone, Long CUIT) {
        this.user = user;
        this.phone = phone;
        this.CUIT = CUIT;
        this.consignees = new ArrayList<>();

        if (Objects.nonNull(this.user) && Objects.nonNull(this.CUIT)) {
            consignees.add(new Consignee(this.user.getName(), this.CUIT));
        }
    }

    private Client() {
    }

    public Optional<Error> addConsignee(Consignee consignee) {
        String consigneeName = consignee.getName();
        long consigneeCUIT = consignee.getCUIT();

        for (Consignee existingConsignee : consignees) {
            if (existingConsignee.getName().equals(consigneeName) || existingConsignee.getCUIT() == consigneeCUIT) {
                return Optional.of(new Error("duplicated_consignee", "consignee with the same name or CUIT already exists"));
            }
        }

        consignees.add(consignee);
        return Optional.empty();
    }
}
