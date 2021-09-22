package com.sub.manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {

    static int time = 10000000;
    static List<Integer> list = new ArrayList<>(time);

    @BeforeAll
    static void addData() {
        for (int i = 0; i < time; i++) {
            list.add(i);
        }
    }

    @RepeatedTest(10)
    void test1() {
        ArrayList<Integer> integers = new ArrayList<>(time);
        long start = System.currentTimeMillis();
        list.forEach(integers::add);
        long end = System.currentTimeMillis() - start;
        System.out.println("time: " + end + "ms");
    }

    @RepeatedTest(10)
    void test2() {
        ArrayList<Integer> integers = new ArrayList<>(time);
        long start = System.currentTimeMillis();
        list.stream().forEach(integers::add);
        long end = System.currentTimeMillis() - start;
        System.out.println("time: " + end + "ms");
    }
}
