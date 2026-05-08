package com.dmx_console.model;

/* Proporciona el perfil completo del equipo
* Es decir, define que tipo de Fixture será,
* es decir que configuracion tendra dentro de su tipo
* */

import java.util.ArrayList;
import java.util.List;

public class FixtureProfile {
    private String name;
    private List<FixtureChannel> channels = new ArrayList<>();

    public FixtureProfile(String name){
        this.name = name;
    }

    public void addChannel(FixtureChannel channel){
        channels.add(channel);
    }

    public List<FixtureChannel> getChannels(){
        return channels;
    }

    public String getName(){
        return name;
    }

    public int size(){
        return channels.size();
    }
}
