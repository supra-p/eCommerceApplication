package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private Long addressId;
    private String street;
    private String buildingName;
    private String city;
    private String state;
    private String pincode;
}
