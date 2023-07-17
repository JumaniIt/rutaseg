package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.Consignee;
import com.jumani.rutaseg.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsigneeRepository extends JpaRepository<Consignee, Long> {
    boolean existsByUserAndCuit(User user, long cuit);

    List<Consignee> findAllByUser(User user);
}