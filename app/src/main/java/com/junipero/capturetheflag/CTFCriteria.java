package com.junipero.capturetheflag;

import android.location.Criteria;

public class CTFCriteria extends Criteria{
    // set ACCURACY_FINE or ACCURACY_COARSE
    private static final int ACCURACY = Criteria.ACCURACY_FINE;
    // boolean utility flags
    private static final boolean ALTITUDE_REQUIRED = false;
    private static final boolean BEARING_REQUIRED = false;
    private static final boolean COST_ALLOWED = false;
    // set the Power management of this criteria
    private static final int POWER_REQUIREMENT = Criteria.POWER_LOW;


    public CTFCriteria() {
        super();
        this.setAccuracy(ACCURACY);
        this.setAltitudeRequired(ALTITUDE_REQUIRED);
        this.setBearingRequired(BEARING_REQUIRED);
        this.setCostAllowed(COST_ALLOWED);
        this.setPowerRequirement(POWER_REQUIREMENT);
    }

}
