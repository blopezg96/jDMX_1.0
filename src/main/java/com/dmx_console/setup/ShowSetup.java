package com.dmx_console.setup;

import com.dmx_console.model.Fixture;
import com.dmx_console.model.FixtureProfile;
import com.dmx_console.factory.FixtureProfiles;

import java.util.ArrayList;
import java.util.List;

public class ShowSetup {

    public static List<Fixture> buildRig(){

        List<Fixture> rig = new ArrayList<>();
        FixtureProfile parProfile = FixtureProfiles.par7(); // selecciona Par7 de la bilbioteca
        FixtureProfile headProfile = FixtureProfiles.head(); // selecciona head de la biblioteca

        int adress = 1;

        // En este ciclo añadimos los 20 parleds de 7 canales cada uno
        for(int i=1; i<=20; i++){
             rig.add(new Fixture("PAR " + i,  //Nombre del PAR = PAR + EL VALOR DE i
                     adress,                        // Direccion que comienza en uno
                     parProfile));                         // Tipo de equipo (Perfil)

            adress += parProfile.size();  // Usa el tamaño real del parProfile, es decir cuntos canales ocupa

        }
        System.out.println("ACTUAL ADRESS: " + (adress - 1));




        for(int i=1; i<=5;i++){
            rig.add(new Fixture("HEAD " + i,
                    adress,
                    headProfile));

            adress += headProfile.size(); // Usamos tamaños real de el "headProfile"
        }

        System.out.println("Heads terminan en la direccion: " + (adress - 1));
        System.out.println("Total de canales usados: " + (adress-1));
        return rig;
    }
}
