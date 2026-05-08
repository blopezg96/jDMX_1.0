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
        System.out.println("Canal RED: " +
                universe.getChannel(headBase + 1)); //255
        System.out.println("Canal GREEN: " +
                universe.getChannel(headBase + 2)); //0
        System.out.println("Canal BLUE: " +
                universe.getChannel(headBase + 3)); //255

        System.out.println("\n ==== TEST PARA BLACKOUT GENERAL====");
        service.setColor(par1, 0, 255, 0); // primero setea a verde
        service.blackoutAll();
        System.out.println("Canal 1 tras blackout all: " +
                universe.getChannel(1)); //0
        System.out.println("Cananl 141 tras blackout all: " +
                universe.getChannel(141)); //0

    }

}
