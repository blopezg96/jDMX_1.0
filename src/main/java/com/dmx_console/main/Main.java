package com.dmx_console.main;

import com.dmx_console.model.Fixture;
import com.dmx_console.dmx.Universe;
import com.dmx_console.output.DMXOutput;
import com.dmx_console.output.SimulatedDMXOutput;
import com.dmx_console.service.FixtureService;
import com.dmx_console.setup.ShowSetup;
import javafx.scene.Scene;

import java.util.List;

public class Main {
    public static void main(String [] args){


        Universe universe = new Universe();
        DMXOutput output = new SimulatedDMXOutput(); // <<<< AQUI CUANDO TENGAMOS ENTTEC



        FixtureService service = new FixtureService(universe, output);

        output.connect();
        List<Fixture> rig = ShowSetup.buildRig();

        System.out.println("====== RIG CREADO ======");
        System.out.println("Total Fixtures: " + rig.size()); //size debe ser 25

        for(Fixture f : rig){
            System.out.println(f.getName()
            + "| Dir: " + f.getAddress()
            + "| Profile: " + f.getProfile().getName()
            + "| Canales: " + f.getProfile().size());
        }

        // Testeando PAR 1 a rojo

        System.out.println("\n===== TEST PAR 1 - ROJO =====");
        Fixture par1 = rig.get(0); //direccion 1
        service.setColor(par1, 255,0,0);
        System.out.println("Canal 1 (DIMMER): " +
                universe.getChannel(1)); //255
        System.out.println("Canal 2 (RED): " +
                universe.getChannel(2)); //255
        System.out.println("Canal 3 (GREEN): " +
                universe.getChannel(3)); //0
        System.out.println("Canal 4 (BLUE): " +
                universe.getChannel(4)); //0

        System.out.println("\n==== TEST HEAD 1 - MAGENTA =====");
        Fixture head1 = rig.get(20);
        service.setColor(head1, 255, 0, 255);
        int headBase = head1.getAddress();
        System.out.println("Head direccion base: " + headBase);
        System.out.println("Canal PAN: " +
                universe.getChannel(headBase));
        System.out.println("Canal TILT: " +
                universe.getChannel(headBase + 1 ));
        System.out.println("Canal COLOR_WHEEL: " +
                universe.getChannel(headBase + 2));
        System.out.println("Canal RED: " +
                universe.getChannel(headBase + 3)); //255
        System.out.println("Canal GREEN: " +
                universe.getChannel(headBase + 4)); //0
        System.out.println("Canal BLUE: " +
                universe.getChannel(headBase + 5)); //255

        System.out.println("\n==== TEST PARA BLACKOUT PAR 1 =====");
        service.blackout(par1);
        output.disconnect();




    }

}
