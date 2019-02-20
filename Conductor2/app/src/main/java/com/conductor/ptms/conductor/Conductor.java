package com.conductor.ptms.conductor;

class Conductor {
    String C_Address,C_Conductor_Name,C_Contact,C_Email,C_ID,C_Password;

    public Conductor(){

    }

    public Conductor(String c_Address, String c_Conductor_Name, String c_Contact, String c_Email, String c_ID, String c_Password) {
        this.C_Address = c_Address;
        this.C_Conductor_Name = c_Conductor_Name;
        this.C_Contact = c_Contact;
        this.C_Email = c_Email;
        this.C_ID = c_ID;
        this.C_Password = c_Password;
    }

    public String getC_Address() {
        return C_Address;
    }

    public String getC_Conductor_Name() {
        return C_Conductor_Name;
    }

    public String getC_Contact() {
        return C_Contact;
    }

    public String getC_Email() {
        return C_Email;
    }

    public String getC_ID() {
        return C_ID;
    }

    public String getC_Password() {
        return C_Password;
    }
}

