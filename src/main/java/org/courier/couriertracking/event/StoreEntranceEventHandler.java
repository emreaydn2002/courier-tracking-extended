package org.courier.couriertracking.event;

import lombok.RequiredArgsConstructor;
import org.courier.couriertracking.domain.Courier;
import org.courier.couriertracking.domain.Store;
import org.courier.couriertracking.domain.StoreEntranceLog;
import org.courier.couriertracking.distance.DistanceCalculator;
import org.courier.couriertracking.repository.CourierRepository;
import org.courier.couriertracking.repository.StoreEntranceLogRepository;
import org.courier.couriertracking.repository.StoreRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreEntranceEventHandler {

    private static final double ENTRANCE_RADIUS_METERS = 100.0;
    private static final Duration REENTRY_WINDOW = Duration.ofMinutes(1);

    private final CourierRepository courierRepository;
    private final StoreRepository storeRepository;
    private final StoreEntranceLogRepository storeEntranceLogRepository;
    private final DistanceCalculator distanceCalculator;

    @EventListener
    @Transactional
    public void onLocationUpdated(LocationUpdatedEvent event) {

        UUID courierId = event.getCourierId();
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new IllegalStateException("Courier not found: " + courierId));

        List<Store> stores = storeRepository.findAll();

        Instant now = event.getTime();

        for (Store store : stores) {
            double distance = distanceCalculator.calculateDistanceMeters(
                    event.getLat(), event.getLng(),
                    store.getLat(), store.getLng()
            );

            if (distance > ENTRANCE_RADIUS_METERS) {
                continue;
            }

            // Aynı kurye + aynı store için son giriş
            var lastEntranceOpt =
                    storeEntranceLogRepository.findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
                            courierId, store.getId()
                    );

            if (lastEntranceOpt.isPresent()) {
                Instant lastTime = lastEntranceOpt.get().getEntranceTime();
                if (!now.isAfter(lastTime.plus(REENTRY_WINDOW))) {
                    // 1 dakika dolmamış → re-entry sayma
                    continue;
                }
            }

            StoreEntranceLog log = new StoreEntranceLog();
            log.setCourier(courier);
            log.setStore(store);
            log.setEntranceTime(now);
            log.setDistanceMeters(distance);

            storeEntranceLogRepository.save(log);
        }
    }
}
