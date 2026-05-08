package com.dmx_console.model;
/* Un fixture es un equipo fisico puede ser un parled, robotica, etc. */

public class Fixture {
    private String name;
    private int adress;
    private FixtureProfile profile;

    public Fixture(String name, int adress, FixtureProfile profile){
        this.name = name;
        this.adress = adress;
        this.profile = profile;
    }

    public int getAdress(){
        return adress;
    }
    public String getName(){
        return name;
    }
    public FixtureProfile getProfile(){
        return profile;
    }
}
