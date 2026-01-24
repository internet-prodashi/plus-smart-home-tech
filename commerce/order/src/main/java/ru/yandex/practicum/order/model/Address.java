package ru.yandex.practicum.order.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @Column(name = "country")
    String country;

    @Column(name = "city")
    String city;

    @Column(name = "street")
    String street;

    @Column(name = "house")
    String house;

    @Column(name = "flat")
    String flat;
}