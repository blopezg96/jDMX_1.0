package com.dmx_console.service;

/* Service de Fixture */

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.model.FixtureChannel;
import com.dmx_console.dmx.Universe;
import com.dmx_console.output.DMXOutput;

public class FixtureService {

    private final Universe universe;  // Instancia de universo
    private final DMXOutput output;

    public FixtureService(Universe universe, DMXOutput output){  //Constructor
        this.universe = universe;
        this.output = output;
    }




    public void setChannel(Fixture fixture, ChannelFunction function,
                           int value){
        setChannel(fixture, function, value, Universe.Source.MANUAL);
    }

    public void setChannel(Fixture fixture, ChannelFunction function,
                           int value, Universe.Source source){
        fixture.getProfile().getChannels().stream()
                .filter(ch -> ch.getFunction() == function)
                .findFirst()
                .ifPresent(ch -> universe.setChannel(
                        fixture.getAddress() + ch.getOffset() - 1, value,
                        source
                ));
    }

    public void setChanelFromUI(Fixture fixture,
                                ChannelFunction function,
                                int value,
                                boolean chaseActive){
        Universe.Source src = chaseActive
                ? Universe.Source.MANUAL_OVERIDE
                : Universe.Source.MANUAL;
        setChannel(fixture, function, value, src);
    }

    public void setColor(Fixture fixture, int r, int g, int b){
        setColor(fixture, r,g,b, Universe.Source.MANUAL);
    }

    // setColor usa setChannel internamente
    public void setColor(Fixture fixture,
                         int r, int g, int b, Universe.Source source){

        applyChannel(fixture, ChannelFunction.RED,r, source);
        applyChannel(fixture, ChannelFunction.GREEN, g, source);
        applyChannel(fixture, ChannelFunction.BLUE, b, source);


    }

    // Blackout apaga todos los canales del fixture.
    public void blackout(Fixture fixture){
        int base = fixture.getAddress();
        for(FixtureChannel ch : fixture.getProfile().getChannels()){
            universe.setChannel(base + ch.getOffset() - 1, 0,
                    Universe.Source.MANUAL_OVERIDE);
        }


    }

    public void blackoutAll(){

        universe.blackout();
    }


    public void applyChannel(Fixture fixture, ChannelFunction function,
                             int value, Universe.Source source){
        fixture.getProfile().getChannels().stream()
                .filter(ch -> ch.getFunction() == function)
                .findFirst()
                .ifPresent(ch -> universe.setChannel(
                        fixture.getAddress() + ch.getOffset() - 1, value, source
                ));
    }



    public int getChannelValue(Fixture fixture, ChannelFunction function){
        return fixture.getProfile().getChannels().stream()
                .filter(ch -> ch.getFunction() == function)
                .findFirst()
                .map(ch -> universe.getChannel(
                        fixture.getAddress() + ch.getOffset() - 1
                ))
                .orElse(0);
    }

    public Universe getUniverse(){
        return universe;
    }

}
