package org.courier.couriertracking.dto.response;

public record StoreResponse(
        String id,
        String name,
        Double lat,
        Double lng
) {}