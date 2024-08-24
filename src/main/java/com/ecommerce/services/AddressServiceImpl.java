package com.ecommerce.services;

import com.ecommerce.dto.AddressDto;
import com.ecommerce.exceptions.resourceNotFoundException;
import com.ecommerce.models.Address;
import com.ecommerce.models.User;
import com.ecommerce.repositories.AddressRepository;
import com.ecommerce.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final ModelMapper modelMapper;
    private final AddressRepository addressRepo;
    private final UserRepository userRepo;

    public AddressServiceImpl(ModelMapper modelMapper, AddressRepository addressRepo, UserRepository userRepo) {
        this.modelMapper = modelMapper;
        this.addressRepo = addressRepo;
        this.userRepo = userRepo;
    }

    @Override
    public AddressDto createAddress(Address addressDto, User user) {
        Address address = modelMapper.map(addressDto, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        addressRepo.save(address);
        return modelMapper.map(addressDto, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAddresses() {
        List<Address> addresses = addressRepo.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public AddressDto getAddressById(Long addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new resourceNotFoundException("Address Not Found"));
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAddressByUser(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public AddressDto updateAddressById(Long addressId, AddressDto addressDto) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new resourceNotFoundException("Address Not Found"));

        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPincode(addressDto.getPincode());
        address.setBuildingName(addressDto.getBuildingName());
        address.setStreet(addressDto.getStreet());

        addressRepo.save(address);
        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        user.getAddresses().add(address);
        userRepo.save(user);

        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new resourceNotFoundException("Address Not Found"));

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        userRepo.save(user);
        addressRepo.delete(address);

        return "Address deleted successfully";
    }

}
