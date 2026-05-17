package com.dmx_console.model;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private String name;

    private Map<String, Map<String, Integer>> fixtureValues;

    public Scene(String name){
        this.name = name;
        this.fixtureValues = new HashMap<>();
    }

    // Guardar el valor de un canal para un fixture
    public void setValues(String fixtureName,
                          ChannelFunction function,
                          int value){
        fixtureValues
                .computeIfAbsent(fixtureName, k -> new HashMap<>())
                .put(function.name(), value);

    }

    //Obtienen en valor de una canal para un fixture
    public int getValue(String fixtureName, ChannelFunction function){
        Map<String, Integer> channels = fixtureValues.get(fixtureName);
        if(channels==null) return 0;
        return channels.getOrDefault(function.name(), 0);

    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public Map<String, Map<String, Integer>> getFixtureValues(){
        return fixtureValues;
    }
}
