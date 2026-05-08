package setup;

import com.dmx_console.model.Fixture;
import com.dmx_console.model.FixtureProfile;
import factory.FixtureProfiles;

import java.util.ArrayList;
import java.util.List;

public class ShowSetup {

    public static List<Fixture> buildRig(){

        List<Fixture> rig = new ArrayList<>();
        FixtureProfile par = FixtureProfiles.par7(); // selecciona Par7 de la bilbioteca
        FixtureProfile head = FixtureProfiles.head(); // selecciona head de la biblioteca

        int adress = 1;

        // En este ciclo añadimos los 20 parleds.
        for(int i=1; i<=20; i++){
             rig.add(new Fixture("PAR " + i,  //Nombre del PAR = PAR + EL VALOR DE i
                     adress,                        // Direccion que comienza en uno
                     par));                         // Tipo de equipo (Perfil)

            adress += 7;

        }
        System.out.println("ACTUAL ADRESS: " + adress);

        int nAdrees = adress +7;
        System.out.println("HEADS INICIAL ADRESS: " + nAdrees);

        for(int i=1; i<=5;i++){
            rig.add(new Fixture("HEAD " + i,
                    nAdrees,
                    head));

            nAdrees += 7;
        }

        System.out.println("20 PARLEDS CREATED USING " + adress + " ADRESS VALUES");
        System.out.println("5 HEADS CREATED USING " + nAdrees + " ADRESS VALUES");
        return rig;
    }
}
