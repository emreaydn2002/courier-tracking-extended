package org.courier.couriertracking.dto;

import org.courier.couriertracking.domain.Courier;
import org.courier.couriertracking.domain.Store;
import org.courier.couriertracking.domain.StoreEntranceLog;
import org.courier.couriertracking.dto.response.CourierEntrancesResponse;
import org.courier.couriertracking.dto.response.CourierResponse;
import org.courier.couriertracking.dto.response.EntranceLogResponse;
import org.courier.couriertracking.dto.response.StoreResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourierMapper {

    public CourierEntrancesResponse toCourierEntrancesResponse(
            Courier courier,
            List<StoreEntranceLog> logs
    ) {
        CourierResponse courierDto = new CourierResponse(courier.getCode());

        List<EntranceLogResponse> logDtos = logs.stream()
                .map(this::toEntranceLogResponse)
                .toList();

        return new CourierEntrancesResponse(courierDto.code(), logDtos);
    }

    private EntranceLogResponse toEntranceLogResponse(StoreEntranceLog log) {
        Store store = log.getStore();

        StoreResponse storeDto = new StoreResponse(
                store.getId().toString(),
                store.getName(),
                store.getLat(),
                store.getLng()
        );

        return new EntranceLogResponse(
                log.getId().toString(),
                storeDto,
                log.getEntranceTime(),
                log.getDistanceMeters()
        );
    }
}
