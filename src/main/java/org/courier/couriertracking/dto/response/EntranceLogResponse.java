package org.courier.couriertracking.dto.response;

import java.time.Instant;

public record EntranceLogResponse(
        String id,
        StoreResponse store,
        Instant entranceTime,
        Double distanceMeters
) {}