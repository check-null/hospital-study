package com.sub.order.component;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AlipayComponentTest {

    @Test
    void test() {
        String orderTime = new DateTime().toString("yyyyMMdd");
        double random = Math.random();
        String s1 = String.valueOf(random).substring(2, 6);
        String s2 = orderTime + "200040878" + s1;
        System.out.println(s2);

        BigDecimal bigDecimal = new BigDecimal("1998.00");
        BigDecimal bigDecimal1 = new BigDecimal("0.098");
        BigDecimal add = bigDecimal.add(bigDecimal1);
        String s = add.toString();

        System.out.println(s);

    }

}
