package com.conductor.ptms.conductor;

public class TicketLog {

    private String b_Source,c_Destination,d_Date,e_No_Of_Ticket,f_Fare;

    public TicketLog(){

    }

    public TicketLog(String b_Source, String c_Destination, String d_Date, String e_No_Of_Ticket, String f_Fare) {
        this.b_Source = b_Source;
        this.c_Destination = c_Destination;
        this.d_Date = d_Date;
        this.e_No_Of_Ticket = e_No_Of_Ticket;
        this.f_Fare = f_Fare;
    }

    public String getB_Source() {
        return b_Source;
    }

    public String getC_Destination() {
        return c_Destination;
    }

    public String getD_Date() {
        return d_Date;
    }

    public String getE_No_Of_Ticket() {
        return e_No_Of_Ticket;
    }

    public String getF_Fare() {
        return f_Fare;
    }
}
