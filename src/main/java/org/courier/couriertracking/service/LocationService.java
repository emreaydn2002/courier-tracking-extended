package org.courier.couriertracking.service;

import lombok.RequiredArgsConstructor;
import org.courier.couriertracking.domain.Courier;
import org.courier.couriertracking.domain.StoreEntranceLog;
import org.courier.couriertracking.dto.request.LocationUpdateRequest;
import org.courier.couriertracking.event.LocationUpdatedEvent;
import org.courier.couriertracking.repository.CourierRepository;
import org.courier.couriertracking.repository.CourierTrackRepository;
import org.courier.couriertracking.repository.StoreEntranceLogRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final CourierRepository courierRepository;
    private final CourierTrackRepository courierTrackRepository;
    private final StoreEntranceLogRepository storeEntranceLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handleLocationUpdate(LocationUpdateRequest request) {
        // courierId = dışarıdan gelen kod (c1, c2, ...)
        String courierCode = request.getCourierId();

        Courier courier = courierRepository.findByCode(courierCode)
                .orElseGet(() -> createCourier(courierCode));

        // Observer pattern: event publish
        LocationUpdatedEvent event = new LocationUpdatedEvent(
                this,
                courier.getId(),
                request.getLat(),
                request.getLng(),
                request.getTime()
        );

        eventPublisher.publishEvent(event);
    }

    public double getTotalTravelDistance(String courierCode) {
        Courier courier = courierRepository.findByCode(courierCode)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found: " + courierCode));

        return courierTrackRepository.findByCourierId(courier.getId())
                .map(t -> t.getTotalDistanceMeters() != null ? t.getTotalDistanceMeters() : 0.0)
                .orElse(0.0);
    }

    public List<StoreEntranceLog> getEntrances(String courierCode) {
        Courier courier = courierRepository.findByCode(courierCode)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found: " + courierCode));

        return storeEntranceLogRepository.findByCourierIdOrderByEntranceTimeAsc(courier.getId());
    }

    private Courier createCourier(String code) {
        Courier courier = new Courier();
        courier.setCode(code);
        courier.setName("Courier " + code);
        return courierRepository.save(courier);
    }
}
