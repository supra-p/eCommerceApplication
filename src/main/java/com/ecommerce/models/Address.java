package com.ecommerce.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long addressId;

    @NotBlank
    private String street;

    @NotBlank
    private String buildingName;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    @Size(min=6, message = "Pincode must be at least 6 digits")
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address() {

    }

    public Address(String street, String buildingName, String city, String state, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }
}
