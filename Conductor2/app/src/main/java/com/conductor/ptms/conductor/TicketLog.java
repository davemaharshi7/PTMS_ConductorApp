package com.conductor.ptms.conductor;

public class TicketLog {

    private String b_Source,c_Destination,d_Date,e_No_Of_Ticket,f_Fare;
    private int g_Payment_mode;
    public TicketLog(){

    }

    public TicketLog(String b_Source, String c_Destination, String d_Date, String e_No_Of_Ticket, String f_Fare, int g_Payment_mode) {
        this.b_Source = b_Source;
        this.c_Destination = c_Destination;
        this.d_Date = d_Date;
        this.e_No_Of_Ticket = e_No_Of_Ticket;
        this.f_Fare = f_Fare;
        this.g_Payment_mode = g_Payment_mode;
    }

    public String getB_Source() {
        return b_Source;
    }

    public void setB_Source(String b_Source) {
        this.b_Source = b_Source;
    }

    public String getC_Destination() {
        return c_Destination;
    }

    public void setC_Destination(String c_Destination) {
        this.c_Destination = c_Destination;
    }

    public String getD_Date() {
        return d_Date;
    }

    public void setD_Date(String d_Date) {
        this.d_Date = d_Date;
    }

    public String getE_No_Of_Ticket() {
        return e_No_Of_Ticket;
    }

    public void setE_No_Of_Ticket(String e_No_Of_Ticket) {
        this.e_No_Of_Ticket = e_No_Of_Ticket;
    }

    public String getF_Fare() {
        return f_Fare;
    }

    public void setF_Fare(String f_Fare) {
        this.f_Fare = f_Fare;
    }

    public int getG_Payment_mode() {
        return g_Payment_mode;
    }

    public void setG_Payment_mode(int g_Payment_mode) {
        this.g_Payment_mode = g_Payment_mode;
    }
}
