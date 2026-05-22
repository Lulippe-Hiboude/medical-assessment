package com.medical.assessment.patientms.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "street_number", nullable = false, length = 20)
    private String streetNumber;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @OneToOne(mappedBy = "address")
    private Patient patient;
}
