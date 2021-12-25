package com.nihanim.softwaretest.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @RequestMapping("/{customerId}")
    public void makePayment(@PathVariable("customerId") UUID customerId, @RequestBody PaymentRequest paymentRequest) {

        paymentService.chargeCard(customerId, paymentRequest);

    }
}
