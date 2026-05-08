package main;

import com.dmx_console.model.Fixture;
import dmx.Universe;
import service.FixtureService;
import setup.ShowSetup;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String [] args){

        Universe universe = new Universe();

        FixtureService service = new FixtureService(universe);

        List<Fixture> rig = ShowSetup.buildRig();

        Fixture par1 = rig.get(0);

        service.setColor(par1, 255,0,0);

        System.out.println(universe.getChannel(1));
        System.out.println(universe.getChannel(2));
        System.out.println(universe.getChannel(3));

    }

}
