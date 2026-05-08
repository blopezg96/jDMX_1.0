package service;

import com.dmx_console.model.Fixture;
import dmx.Universe;

public class FixtureService {

    private final Universe universe;  // Instancia de universo

    public FixtureService(Universe universe){  //Constructor
        this.universe = universe;
    }

    public void setColor(Fixture fixture,      //Asigna color rgb
                         int r, int g, int b){

        int base =  fixture.getAddress();
        universe.setChannel(base, 255);
        universe.setChannel(base + 1,r);
        universe.setChannel(base + 2,g);
        universe.setChannel(base + 3,b);



    }

    public void blackout(Fixture fixture){
        int base = fixture.getAddress();
        universe.setChannel(base,0);

    }

}
