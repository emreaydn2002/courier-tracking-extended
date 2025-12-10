package org.courier.couriertracking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.courier.couriertracking.domain.Courier;
import org.courier.couriertracking.domain.StoreEntranceLog;
import org.courier.couriertracking.dto.request.LocationUpdateRequest;
import org.courier.couriertracking.dto.response.CourierEntrancesResponse;
import org.courier.couriertracking.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/locations")
    public ResponseEntity<Void> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        locationService.handleLocationUpdate(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/couriers/{courierCode}/distance")
    public ResponseEntity<Double> getTotalDistance(@PathVariable String courierCode) {
        double distance = locationService.getTotalTravelDistance(courierCode);
        return ResponseEntity.ok(distance);
    }
}
