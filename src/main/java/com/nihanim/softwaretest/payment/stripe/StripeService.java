package com.nihanim.softwaretest.payment.stripe;

import com.nihanim.softwaretest.payment.CardPaymentCharge;
import com.nihanim.softwaretest.payment.CardPaymentCharger;
import com.nihanim.softwaretest.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService implements CardPaymentCharger {
    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    private final StripeApi stripeApi;

    @Override
    public CardPaymentCharge chargeCard(String source, BigDecimal amount, Currency currency, String description) {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", source);
        params.put("description", description);

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            Boolean chargePaid = charge.getPaid();
            return new CardPaymentCharge(chargePaid);
        } catch (StripeException e) {
            throw new IllegalStateException("Cannot make stripe charge", e);
        }
    }
}
