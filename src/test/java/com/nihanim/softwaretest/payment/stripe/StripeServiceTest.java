package com.nihanim.softwaretest.payment.stripe;

import com.nihanim.softwaretest.payment.CardPaymentCharge;
import com.nihanim.softwaretest.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    private StripeService underTest;
    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        underTest = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        // Given
        String cardSource = "x0x0";
        BigDecimal amount = new BigDecimal("10");
        Currency usd = Currency.USD;
        String donation = "Donation";

        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);

        // When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, usd, donation);

        // Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());

        Map<String, Object> requestMap = mapArgumentCaptor.getValue();

        assertThat(requestMap.keySet()).hasSize(4);

        assertThat(requestMap.get("amount")).isEqualTo(amount);
        assertThat(requestMap.get("currency")).isEqualTo(usd);
        assertThat(requestMap.get("source")).isEqualTo(cardSource);
        assertThat(requestMap.get("description")).isEqualTo(donation);

        RequestOptions requestOptions = optionsArgumentCaptor.getValue();
        assertThat(requestOptions).isNotNull();

        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }
}