package org.courier.couriertracking.dto.response;

import java.time.Instant;

public record StoreEntranceLogResponse(
        String storeName,
        Double storeLat,
        Double storeLng,
        Instant entranceTime,
        Double distanceMeters
) { }
