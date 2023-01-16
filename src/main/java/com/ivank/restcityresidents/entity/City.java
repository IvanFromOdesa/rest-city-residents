package com.ivank.restcityresidents.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "city")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NonNull // Set lombok's @NonNull as default
    private Long id;

    @Column(name = "official_name")
    @NonNull
    private String officialName;

    @Column(name = "population")
    @NonNull
    private Long population;

    @Column(name = "area")
    @NonNull
    private Double area;

    @Column(name = "time_zone")
    @NonNull
    private CityTimeZone timeZone;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference // avoid both sides to be serialized (which causes infinite recursion)
    // We use DTOs, but to be sure
    private List<Resident> residents;
}
