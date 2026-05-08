package service;

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.Fixture;
import com.dmx_console.model.FixtureChannel;
import com.sun.jdi.connect.spi.Connection;
import dmx.Universe;

public class FixtureService {

    private final Universe universe;  // Instancia de universo

    public FixtureService(Universe universe){  //Constructor
        this.universe = universe;
    }

    public void setChannel(Fixture fixture, ChannelFunction function,
                           int value){
        fixture.getProfile().getChannels().stream()
                .filter(ch -> ch.getFunction() == function)
                .findFirst()
                .ifPresent(ch -> universe.setChannel(
                        fixture.getAddress() + ch.getOffset() - 1,
                        value
                ));
    }

    // setColor usa setChannel internamente
    public void setColor(Fixture fixture,      //Asigna color rgb
                         int r, int g, int b){

        setChannel(fixture, ChannelFunction.DIMMER, 255); //Dimmer al maximo
        setChannel(fixture, ChannelFunction.RED, r);
        setChannel(fixture, ChannelFunction.GREEN, g);
        setChannel(fixture, ChannelFunction.BLUE, b);



    }

    // Blackout apaga todos los canales del fixture.
    public void blackout(Fixture fixture){
        int base = fixture.getAddress();
        for(FixtureChannel ch : fixture.getProfile().getChannels()){
            universe.setChannel(base + ch.getOffset() - 1, 0);
        }

    }

    public void blackoutAll(){
        universe.blackout();
    }

}
