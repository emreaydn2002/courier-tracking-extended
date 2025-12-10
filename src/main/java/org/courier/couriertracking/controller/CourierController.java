package org.courier.couriertracking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.courier.couriertracking.domain.StoreEntranceLog;
import org.courier.couriertracking.dto.request.LocationUpdateRequest;
import org.courier.couriertracking.dto.response.CourierEntrancesResponse;
import org.courier.couriertracking.service.CourierQueryService;
import org.courier.couriertracking.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierQueryService courierQueryService;

    @GetMapping("/{code}/entrances")
    public CourierEntrancesResponse getCourierEntrances(@PathVariable String code) {
        return courierQueryService.getCourierEntrances(code);
    }
}
