package com.foodapp.address;

import com.foodapp.address.dto.*;
import com.foodapp.common.exception.ResourceNotFoundException;
import com.foodapp.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressDto> getUserAddresses(String email) {
        User user = findUser(email);
        return addressRepository.findByUserId(user.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AddressDto addAddress(String email, AddressRequest request) {
        User user = findUser(email);

        // If this is set as default, unset existing defaults
        if (request.isDefault()) {
            addressRepository.findByUserId(user.getId())
                    .forEach(a -> { a.setDefault(false); addressRepository.save(a); });
        }

        Address address = Address.builder()
                .user(user)
                .label(request.getLabel())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefault(request.isDefault())
                .build();

        return toDto(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = findUser(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new com.foodapp.common.exception.BadRequestException("Not your address");
        }
        addressRepository.delete(address);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private AddressDto toDto(Address a) {
        return AddressDto.builder()
                .id(a.getId())
                .label(a.getLabel())
                .street(a.getStreet())
                .city(a.getCity())
                .state(a.getState())
                .zipCode(a.getZipCode())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .isDefault(a.isDefault())
                .build();
    }
}
