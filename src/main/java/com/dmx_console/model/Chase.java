package com.dmx_console.model;

import java.util.ArrayList;
import java.util.List;

public class Chase {

    private String name;
    private List<Scene> steps;
    private double bpm;
    private ChaseMode mode;

    public Chase(String name, double bpm, ChaseMode mode){
        this.name = name;
        this.bpm = bpm;
        this.mode = mode;
        this.steps = new ArrayList<>();
    }

    public void addStep(Scene scene){
        steps.add(scene);
    }
    public void removeStep(int index){
        if(index >= 0 && index < steps.size()){
            steps.remove(index);
        }
    }

    public long getIntervalMs(){
        return (long)(60000.0/bpm);
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Scene> getSteps() {
        return steps;
    }

    public double getBpm() {
        return bpm;
    }
    public void setBpm(double bpm){
        this.bpm = bpm;
    }

    public ChaseMode getMode() {
        return mode;
    }

    public void setMode(ChaseMode mode) {
        this.mode = mode;
    }
}
