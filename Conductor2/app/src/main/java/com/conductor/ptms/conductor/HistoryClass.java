package com.conductor.ptms.conductor;

public class HistoryClass {
    private String Bus_ID,Date,Route_ID,Total_Fare,Total_Tickets,Transaction_Complete,Destination,Source;

    public HistoryClass(){

    }

    public HistoryClass(String bus_ID, String date, String route_ID, String total_Fare, String total_Tickets, String transaction_Complete, String destination, String source) {
        Bus_ID = bus_ID;
        Date = date;
        Route_ID = route_ID;
        Total_Fare = total_Fare;
        Total_Tickets = total_Tickets;
        Transaction_Complete = transaction_Complete;
        Destination = destination;
        Source = source;
    }

    public String getBus_ID() {
        return Bus_ID;
    }

    public String getDate() {
        return Date;
    }

    public String getRoute_ID() {
        return Route_ID;
    }

    public String getTotal_Fare() {
        return Total_Fare;
    }

    public String getTotal_Tickets() {
        return Total_Tickets;
    }

    public String getTransaction_Complete() {
        return Transaction_Complete;
    }

    public String getDestination() {
        return Destination;
    }

    public String getSource() {
        return Source;
    }
}
