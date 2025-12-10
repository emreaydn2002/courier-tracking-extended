package org.courier.couriertracking.repository;

import org.courier.couriertracking.domain.Courier;
import org.courier.couriertracking.domain.StoreEntranceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreEntranceLogRepository extends JpaRepository<StoreEntranceLog, UUID> {

    List<StoreEntranceLog> findByCourierIdOrderByEntranceTimeAsc(UUID courierId);

    Optional<StoreEntranceLog> findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
            UUID courierId,
            UUID storeId
    );

    List<StoreEntranceLog> findByCourier_IdOrderByEntranceTimeDesc(UUID courierId);
}
