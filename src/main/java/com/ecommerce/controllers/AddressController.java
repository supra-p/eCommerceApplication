package com.ecommerce.controllers;

import com.ecommerce.dto.AddressDto;
import com.ecommerce.models.Address;
import com.ecommerce.models.User;
import com.ecommerce.services.AddressService;
import com.ecommerce.utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AuthUtil authUtil;
    private final AddressService addressService;

    public AddressController(AuthUtil authUtil, AddressService addressService) {
        this.authUtil = authUtil;
        this.addressService = addressService;
    }

    @PostMapping("/addresses")
    public AddressDto createAddress(@Valid @RequestBody Address addressDto){
        User user = authUtil.loggedInUser();
        return addressService.createAddress(addressDto, user);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses(){
        List<AddressDto> addressDtos = addressService.getAddresses();
        return ResponseEntity.ok(addressDtos);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long addressId){
        AddressDto addressDto = addressService.getAddressById(addressId);
        return ResponseEntity.ok(addressDto);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDto>> getAddressById(){
        User user = authUtil.loggedInUser();
        List<AddressDto> addressDtos = addressService.getAddressByUser(user);
        return ResponseEntity.ok(addressDtos);
    }

    @PutMapping("/user/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressDto addressDto
    ){
        AddressDto addressDtoNew = addressService.updateAddressById(addressId, addressDto);
        return ResponseEntity.ok(addressDtoNew);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId){
        String message = addressService.deleteAddressById(addressId);
        return ResponseEntity.ok(message);
    }

}
