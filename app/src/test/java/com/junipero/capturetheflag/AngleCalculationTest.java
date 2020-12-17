package com.junipero.capturetheflag;

import org.junit.Test;

public class AngleCalculationTest {

    public double sinAdjusted (double x){
        return Math.sin(Math.toRadians(x));
    }

    public double cosAdjusted (double x){
        return Math.cos(Math.toRadians(x));
    }

    @Test
    public void calculateAngleTest(){
        double myLat = 45.483269;
        double myLong = 12.233232;
        double destLat = 45.39991951798215;
        double destLong = 11.511222781249959;

        // calculate X
        double x = Math.cos(Math.toRadians(destLat)) * Math.sin(Math.toRadians(destLong - myLong));
        x = cosAdjusted(destLat) * sinAdjusted(destLong - myLong);
        System.out.println("X: " + x);


        // calculate Y
        double y = cosAdjusted(myLat) * sinAdjusted(destLat) - sinAdjusted(myLat) * cosAdjusted(destLat) * cosAdjusted(destLong - myLong);
        System.out.println("Y: " + y);


        // last Calc
        double res = Math.atan2(x, y);
        res = Math.toDegrees(res);
        res = (res < 0) ? 360 + res : res;
        System.out.println(res);
    }
}
