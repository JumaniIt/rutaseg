package com.jumani.rutaseg.domain;

import com.jumani.rutaseg.dto.result.Error;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "phone")
    private String phone;
    @Column(name = "cuit")
    private Long CUIT;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "consignees",
            joinColumns = @JoinColumn(name = "client_id"))
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

    public Long getUserId() {
        return Optional.ofNullable(this.user).map(User::getId).orElse(null);
    }
}
