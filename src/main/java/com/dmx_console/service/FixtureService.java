package com.dmx_console.service;

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

    /*
    public void setChannel(Fixture fixture, ChannelFunction function,
                           int value){
        fixture.getProfile().getChannels().stream()
                .filter(ch -> ch.getFunction() == function)
                .findFirst()
                .ifPresent(ch -> universe.setChannel(
                        fixture.getAddress() + ch.getOffset() - 1,
                        value
                ));
        send();
    } */

    // setColor usa setChannel internamente
    public void setColor(Fixture fixture,      //Asigna color rgb
                         int r, int g, int b){


        applyChannel(fixture, ChannelFunction.RED,r);
        applyChannel(fixture, ChannelFunction.GREEN, g);
        applyChannel(fixture, ChannelFunction.BLUE, b);
        send(); // Un solo envio al final.



    }

    // Blackout apaga todos los canales del fixture.
    public void blackout(Fixture fixture){
        int base = fixture.getAddress();
        for(FixtureChannel ch : fixture.getProfile().getChannels()){
            universe.setChannel(base + ch.getOffset() - 1, 0);
        }
        send();

    }

    public void blackoutAll(){
        universe.blackout();
        send();
    }

    private void send(){
        byte[] data = new byte[512];
        for(int i = 0; i<512; i++){
            data[i] = (byte) universe.getChannel(i+1);
        }
        output.sendUniverse(data);
    }

    public void applyChannel(Fixture fixture, ChannelFunction function,
                             int value){
        fixture.getProfile().getChannels().stream()
                .filter(ch -> ch.getFunction() == function)
                .findFirst()
                .ifPresent(ch -> universe.setChannel(
                        fixture.getAddress() + ch.getOffset() - 1, value
                ));
    }

    //SetChannel publico que sigue enviando inmediantamente(solo cambios individuales)
    public void setChannel(Fixture fixture, ChannelFunction function,
                           int value){
        applyChannel(fixture, function, value);
        send();
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

}
