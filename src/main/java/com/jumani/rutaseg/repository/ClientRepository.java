package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findOneByUser_Id(long userId);
}
