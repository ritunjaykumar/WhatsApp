package com.softgyan.whatsapp.models;

public class Contacts {
    private final String name;
    private final String number;

    public Contacts(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
