package com.nihanim.softwaretest.payment;

import com.nihanim.softwaretest.customer.Customer;
import com.nihanim.softwaretest.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;
    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldCardCharge() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Payment payment = new Payment(null, customerId, new BigDecimal("10"),
                Currency.USD, "card123", "Donation");

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(customerId, paymentRequest);

        // Then
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptureValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptureValue).isEqualTo(payment);
    }

    @Test
    void itShouldThrowWhenCardNotCharge() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Payment payment = new Payment(null, customerId, new BigDecimal("10"),
                Currency.USD, "card123", "Donation");

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Card not debit for the customer %s", customerId);
        then(paymentRepository).should(never()).save(any(Payment.class));
    }

    @Test
    void itShouldNotChargeWhenCurrencyNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Payment payment = new Payment(null, customerId, new BigDecimal("10"),
                Currency.EUR, "card123", "Donation");

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Currency [%s] not supported", paymentRequest.getPayment().getCurrency());
        then(paymentRepository).should(never()).save(any(Payment.class));
    }

    @Test
    void itShouldNotChargeWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        Payment payment = new Payment(null, customerId, new BigDecimal("10"),
                Currency.USD, "card123", "Donation");

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer [%s] not found", customerId);
        then(paymentRepository).should(never()).save(any(Payment.class));
    }
}