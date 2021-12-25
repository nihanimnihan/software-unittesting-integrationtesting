package com.nihanim.softwaretest.customer;

import com.nihanim.softwaretest.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    private CustomerRegistrationService underTest;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given
        String phoneNumber = "000";
        Customer customer = Customer.builder().id(UUID.randomUUID()).name("Abc").phone(phoneNumber).build();

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findByPhone(phoneNumber)).willReturn(Optional.empty());

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        // When
        underTest.registerCustomer(request);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIDNull() {
        // Given
        String phoneNumber = "000";
        Customer customer = Customer.builder().id(null).name("Abc").phone(phoneNumber).build();

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findByPhone(phoneNumber)).willReturn(Optional.empty());

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        // When
        underTest.registerCustomer(request);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given
        String phoneNumber = "000";
        Customer customer = Customer.builder().id(UUID.randomUUID()).name("Abc").phone(phoneNumber).build();

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findByPhone(phoneNumber)).willReturn(Optional.of(customer));

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        // When
        underTest.registerCustomer(request);

        // Then
        then(customerRepository).should(never()).save(any());
        then(customerRepository).should().findByPhone(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        // Given
        String phoneNumber = "000";
        Customer customer = Customer.builder().id(UUID.randomUUID()).name("Abc").phone(phoneNumber).build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).name("Bcd").phone(phoneNumber).build();

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findByPhone(phoneNumber)).willReturn(Optional.of(customer2));

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone number [%s] is taken", phoneNumber));
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNotValid() {
        // Given
        String phoneNumber = "000";
        Customer customer = Customer.builder().id(UUID.randomUUID()).name("Abc").phone(phoneNumber).build();

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone number [%s] is not valid", phoneNumber));
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}