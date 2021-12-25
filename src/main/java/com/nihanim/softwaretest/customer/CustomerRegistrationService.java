package com.nihanim.softwaretest.customer;

import com.nihanim.softwaretest.utils.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    public void registerCustomer(CustomerRegistrationRequest request) {

        String phoneNumber = request.getCustomer().getPhone();

        if (!phoneNumberValidator.test(phoneNumber)) {
            throw new IllegalStateException(String.format("Phone number [%s] is not valid", phoneNumber));
        }

        Optional<Customer> optionalCustomer = customerRepository.findByPhone(phoneNumber);
        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }
        if (optionalCustomer.isEmpty()) {
            customerRepository.save(request.getCustomer());
            return;
        }
        else if (optionalCustomer.get().getName().equals(request.getCustomer().getName())) {
            return;
        }
        throw new IllegalStateException(String.format("Phone number [%s] is taken", phoneNumber));
    }

}