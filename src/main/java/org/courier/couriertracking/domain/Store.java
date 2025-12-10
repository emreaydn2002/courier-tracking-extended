package org.courier.couriertracking.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "store")
public class Store extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;      // "Ataşehir MMM Migros" vs.

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StoreEntranceLog> entranceLogs = new ArrayList<>();
}
