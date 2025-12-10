package org.courier.couriertracking.event;

import lombok.RequiredArgsConstructor;
import org.courier.couriertracking.domain.Courier;
import org.courier.couriertracking.domain.CourierTrack;
import org.courier.couriertracking.distance.DistanceCalculator;
import org.courier.couriertracking.repository.CourierRepository;
import org.courier.couriertracking.repository.CourierTrackRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocationDistanceEventHandler {

    private final CourierRepository courierRepository;
    private final CourierTrackRepository courierTrackRepository;
    private final DistanceCalculator distanceCalculator;

    @EventListener
    @Transactional
    public void onLocationUpdated(LocationUpdatedEvent event) {

        UUID courierId = event.getCourierId();

        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new IllegalStateException("Courier not found: " + courierId));

        CourierTrack track = courierTrackRepository.findByCourierId(courierId)
                .orElseGet(() -> {
                    CourierTrack t = new CourierTrack();
                    t.setCourier(courier);
                    t.setTotalDistanceMeters(0.0);
                    return t;
                });

        Double lastLat = track.getLastLat();
        Double lastLng = track.getLastLng();

        if (lastLat != null && lastLng != null) {
            double delta = distanceCalculator.calculateDistanceMeters(
                    lastLat, lastLng,
                    event.getLat(), event.getLng()
            );
            track.setTotalDistanceMeters(track.getTotalDistanceMeters() + delta);
        }

        track.setLastLat(event.getLat());
        track.setLastLng(event.getLng());
        track.setLastTime(event.getTime());

        courierTrackRepository.save(track);
    }
}
