package com.nihanim.softwaretest.payment.stripe;

import com.nihanim.softwaretest.payment.CardPaymentCharge;
import com.nihanim.softwaretest.payment.CardPaymentCharger;
import com.nihanim.softwaretest.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false"
)
public class MockStripeService implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String source, BigDecimal amount, Currency currency, String description) {
        return new CardPaymentCharge(true);
    }
}
