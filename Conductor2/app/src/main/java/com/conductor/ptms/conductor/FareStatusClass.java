package com.conductor.ptms.conductor;

public class FareStatusClass {
    private String Bus_ID,C_ID,Date,Is_Collected;
    public FareStatusClass(){

    }

    public FareStatusClass(String bus_ID, String c_ID, String date, String is_Collected) {
        Bus_ID = bus_ID;
        C_ID = c_ID;
        Date = date;
        Is_Collected = is_Collected;
    }

    public String getBus_ID() {
        return Bus_ID;
    }

    public String getC_ID() {
        return C_ID;
    }

    public String getDate() {
        return Date;
    }

    public String getIs_Collected() {
        return Is_Collected;
    }
}
