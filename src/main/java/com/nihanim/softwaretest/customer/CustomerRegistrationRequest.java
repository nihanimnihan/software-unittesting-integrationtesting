package com.nihanim.softwaretest.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CustomerRegistrationRequest {

    private final Customer customer;

    public CustomerRegistrationRequest(@JsonProperty("customer") Customer customer) {
        this.customer = customer;
    }


}
