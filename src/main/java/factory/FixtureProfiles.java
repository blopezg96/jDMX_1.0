package factory;

/* FIXTURES LIBRARIES   */

import com.dmx_console.model.ChannelFunction;
import com.dmx_console.model.FixtureChannel;
import com.dmx_console.model.FixtureProfile;

public class FixtureProfiles {

    public static FixtureProfile par7(){

 /* Creamos un fixture referente a un Par de 7 canales */

        FixtureProfile par7 =
                new FixtureProfile("PAR 7CH");

        /**  Añadimos a par7 los siete canales que lo conforman
         Cada uno, posee diferente nombre. **/


        par7.addChannel(new FixtureChannel(1, ChannelFunction.DIMMER));
        par7.addChannel(new FixtureChannel(2, ChannelFunction.RED));
        par7.addChannel(new FixtureChannel(3, ChannelFunction.GREEN));
        par7.addChannel(new FixtureChannel(4, ChannelFunction.BLUE));
        par7.addChannel(new FixtureChannel(5, ChannelFunction.WHITE));
        par7.addChannel(new FixtureChannel(6, ChannelFunction.YELLOW));
        par7.addChannel(new FixtureChannel(7, ChannelFunction.STROBE));


        return par7;

    }

    public static FixtureProfile head(){

        FixtureProfile head = new FixtureProfile("Spot Head");

        head.addChannel(new FixtureChannel(1, ChannelFunction.PAN));
        head.addChannel((new FixtureChannel(2,ChannelFunction.TILT)));
        head.addChannel(new FixtureChannel(3, ChannelFunction.COLOR_WHEEL));

        return head;

    }

}
