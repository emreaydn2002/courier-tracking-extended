package org.courier.couriertracking.dto.response;


import java.util.List;

public record CourierEntrancesResponse(
        String courierCode,
        List<EntranceLogResponse> entrances
) {
}