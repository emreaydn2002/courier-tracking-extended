package org.courier.couriertracking.domain;

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
@Table(name = "courier")
public class Courier extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;      // örn: "c1"

    @Column(name = "name", length = 255)
    private String name;      // örn: "Courier 1"

    // İstersen kullanırsın, şimdilik LAZY bırakıyoruz.
    @OneToOne(mappedBy = "courier", fetch = FetchType.LAZY)
    private CourierTrack track;

    @OneToMany(mappedBy = "courier", fetch = FetchType.LAZY)
    private List<StoreEntranceLog> entranceLogs = new ArrayList<>();
}
