package org.courier.couriertracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.courier.couriertracking.domain.Courier;
import org.courier.couriertracking.domain.StoreEntranceLog;
import org.courier.couriertracking.dto.CourierMapper;
import org.courier.couriertracking.dto.response.CourierEntrancesResponse;
import org.courier.couriertracking.repository.CourierRepository;
import org.courier.couriertracking.repository.StoreEntranceLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional()
public class CourierQueryService {

    private final CourierRepository courierRepository;
    private final StoreEntranceLogRepository storeEntranceLogRepository;
    private final CourierMapper courierMapper;   // DTO mapper (record'lara çeviriyor)

    public CourierEntrancesResponse getCourierEntrances(String courierCode) {
        Courier courier = courierRepository.findByCode(courierCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Courier not found: " + courierCode));

        List<StoreEntranceLog> logs = storeEntranceLogRepository
                .findByCourier_IdOrderByEntranceTimeDesc(courier.getId());

        return courierMapper.toCourierEntrancesResponse(courier, logs);
    }
}