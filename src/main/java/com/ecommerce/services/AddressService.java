package com.ecommerce.services;

import com.ecommerce.dto.AddressDto;
import com.ecommerce.models.Address;
import com.ecommerce.models.User;

import java.util.List;

public interface AddressService {
    AddressDto createAddress(Address addressDto, User user);

    List<AddressDto> getAddresses();

    AddressDto getAddressById(Long addressId);

    List<AddressDto> getAddressByUser(User user);

    AddressDto updateAddressById(Long addressId, AddressDto addressDto);

    String deleteAddressById(Long addressId);
}
