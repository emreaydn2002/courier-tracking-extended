package org.courier.couriertracking.repository;

import org.courier.couriertracking.domain.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourierRepository extends JpaRepository<Courier, UUID> {

    Optional<Courier> findByCode(String code);
}
