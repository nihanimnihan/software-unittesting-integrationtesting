package com.nihanim.softwaretest.payment;

import com.nihanim.softwaretest.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(new Currency[]{Currency.GBP, Currency.USD});

    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {

        boolean isCustomerPresent = customerRepository.findById(customerId).isPresent();
        if (!isCustomerPresent) {
            String message = String.format("Customer [%s] not found", customerId);
            throw new IllegalStateException(message);
        }

        boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream()
                .anyMatch(c -> c.equals(paymentRequest.getPayment().getCurrency()));
        if (!isCurrencySupported) {
            String message = String.format("Currency [%s] not supported", paymentRequest.getPayment().getCurrency());
            throw new IllegalStateException(message);
        }

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        if (!cardPaymentCharge.isCardDebited()) {
            String message = String.format("Card not debit for the customer %s", customerId);
            throw new IllegalStateException(message);
        }

        paymentRequest.getPayment().setCustomerId(customerId);
        paymentRepository.save(paymentRequest.getPayment());
    }
}
