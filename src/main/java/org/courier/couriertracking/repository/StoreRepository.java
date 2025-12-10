package org.courier.couriertracking.repository;

import org.courier.couriertracking.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    Optional<Store> findByName(String name);
}
