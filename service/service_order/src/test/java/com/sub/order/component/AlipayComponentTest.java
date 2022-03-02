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
        int nextInt = new Random().nextInt(100);
        String o = orderTime + nextInt;
        System.out.println(o);


        BigDecimal bigDecimal = new BigDecimal("1998.00");
        BigDecimal bigDecimal1 = new BigDecimal("0.098");
        BigDecimal add = bigDecimal.add(bigDecimal1);
        String s = add.toString();

        System.out.println(s);

        for (int j = 0; j < 20; j++) {
            int i = new Random().nextInt(100);
            System.out.println(i);
        }
    }

}
