package org.courier.couriertracking.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public class LocationUpdatedEvent extends ApplicationEvent {

    private final UUID courierId;
    private final double lat;
    private final double lng;
    private final Instant time;

    public LocationUpdatedEvent(Object source,
                                UUID courierId,
                                double lat,
                                double lng,
                                Instant time) {
        super(source);
        this.courierId = courierId;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public UUID getCourierId() {
        return courierId;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public Instant getTime() {
        return time;
    }
}
