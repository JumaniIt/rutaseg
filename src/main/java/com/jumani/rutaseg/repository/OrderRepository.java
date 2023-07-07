package com.jumani.rutaseg.repository;

import com.jumani.rutaseg.domain.Order;
import com.jumani.rutaseg.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}



