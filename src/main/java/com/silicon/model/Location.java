package com.silicon.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "locations")


public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column (name = "route_index", nullable = false, unique = true)
    private int routeIndex;

    @Column (name = "city_name", nullable = false)
    private String cityName;

    @Column (name = "description")
    private String description;

    @Column (name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column (name = "longitude", nullable = false)
    private BigDecimal longitude;
}
