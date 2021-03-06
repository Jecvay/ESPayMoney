package com.jecvay.ecosuites.espaymoney.Utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PriceRangeTest {

    private final static double delta = 0.01;

    @Test
    public void number() {
        assertEquals(new PriceRange("16.3").get(), 16.3D, delta);
    }

    @Test
    public void numberNegative() {
        assertEquals(new PriceRange("-16.3").get(), -16.3D, delta);
    }

    @Test
    public void numberSpaces() {
        assertEquals(new PriceRange("  16.3  ").get(), 16.3D, delta);
    }

    @Test
    public void random() {
        PriceRange pr = new PriceRange("-10 ~ 20");
        for (int i = 0; i < 10000; i++) {
            double value = pr.get();
            assertTrue(-10 <= value && value <= 20);
        }
    }
}