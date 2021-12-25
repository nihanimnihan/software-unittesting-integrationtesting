package com.nihanim.softwaretest.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/customer-registration")
@RequiredArgsConstructor
public class CustomerRegistrationController {

    private final CustomerRegistrationService customerRegistrationService;

    @PutMapping
    public void register(@Valid @RequestBody CustomerRegistrationRequest request) {
        customerRegistrationService.registerCustomer(request);
    }

}
