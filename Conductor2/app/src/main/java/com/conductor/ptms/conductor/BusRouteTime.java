package com.conductor.ptms.conductor;

public class BusRouteTime {
    private String Bus_ID,Departure_time,Route_ID;

    BusRouteTime(){

    }

    public BusRouteTime(String bus_ID, String departure_time, String route_ID) {
        Bus_ID = bus_ID;
        Departure_time = departure_time;
        Route_ID = route_ID;
    }

    public String getBus_ID() {
        return Bus_ID;
    }

    public String getDeparture_time() {
        return Departure_time;
    }

    public String getRoute_ID() {
        return Route_ID;
    }
}
