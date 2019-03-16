package com.jecvay.ecosuites.espaymoney.Utils;

import java.util.Random;

public class PriceRange {
    private double minValue;
    private double maxValue;
    Random random;

    public PriceRange(String rangeString) {
        rangeString = rangeString.replace("\\s+", "");
        String[] valueList = rangeString.split("~");
        if (valueList.length == 1) {
            minValue = maxValue = Double.valueOf(valueList[0]);
        } else if (valueList.length == 2) {
            minValue = Double.valueOf(valueList[0]);
            maxValue = Double.valueOf(valueList[1]);
            sortValues();
        }
        random = new Random();
    }

    private void sortValues() {
        if (minValue > maxValue) {
            double t = maxValue;
            maxValue = minValue;
            minValue = t;
        }
    }

    public double getMin() {
        return minValue;
    }

    public double getMax() {
        return maxValue;
    }

    public double getRandom() {
        if (minValue == maxValue) {
            return minValue;
        } else {
            return minValue + (maxValue - minValue) * random.nextDouble();
        }
    }

    public double get() {
        return getRandom();
    }
}
