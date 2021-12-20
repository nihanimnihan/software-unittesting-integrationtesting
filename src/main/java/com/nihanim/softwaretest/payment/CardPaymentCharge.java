package com.nihanim.softwaretest.payment;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
public class CardPaymentCharge {

    private final boolean isCardDebited;

}
