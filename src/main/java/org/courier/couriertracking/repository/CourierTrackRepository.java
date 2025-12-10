package org.courier.couriertracking.repository;

import org.courier.couriertracking.domain.CourierTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourierTrackRepository extends JpaRepository<CourierTrack, UUID> {

    Optional<CourierTrack> findByCourierId(UUID courierId);
}
