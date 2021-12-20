package com.nihanim.softwaretest.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldFindByPhone() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer  = Customer.builder().id(id).name("Abc").phone("0000").build();

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = underTest.findByPhone("0000");
        assertThat(optionalCustomer)
                .isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo("Abc");
                });
    }

    @Test
    void itShouldNotFindByPhone() {
        // Given
        // When
        // Then
        Optional<Customer> customerOptional = underTest.findByPhone("0000");
        assertThat(customerOptional)
                .isNotPresent();
    }

    @Test
    void itShouldSave() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer  = Customer.builder().id(id).name("Abc").phone("0000").build();

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> customerOptional = underTest.findById(id);
        assertThat(customerOptional)
                .isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo("Abc");
                    assertThat(c.getPhone()).isEqualTo("0000");
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).name(null).phone("000").build();

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer));
    }
}