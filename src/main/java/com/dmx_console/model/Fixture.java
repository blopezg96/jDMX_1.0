package com.dmx_console.model;
/* Un fixture es un equipo fisico puede ser un parled, robotica, etc. */

public class Fixture {
    private String name;
    private int address;
    private FixtureProfile profile;

    public Fixture(String name, int address, FixtureProfile profile){
        this.name = name;
        this.address = address;
        this.profile = profile;
    }

    public int getAddress(){
        return address;
    }
    public String getName(){
        return name;
    }
    public FixtureProfile getProfile(){
        return profile;
    }
}
