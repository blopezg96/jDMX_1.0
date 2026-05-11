package com.dmx_console.dmx;

/* Representa nuestra estructura DMX */
public class Universe {


    private final int [] channels = new int[512];  // Canales del universo dmx

    public void setChannel(int channel,  // Asigna a un canal, un valor entre 1 y 255
                           int value){
        if(channel < 1 || channel > 512)
            return;
        if(value < 0) value = 0;
        if(value > 255) value = 255;

        channels[channel -1] = value;

    }
    public int getChannel(int channel){   // Retorna el valor del canal solicitado
        if(channel <1 || channel >512)
            return 0;


        return channels[channel - 1];

    }


    public void blackout(){           // Setea todos los valores de cada canal en 0
        for(int i =0; i<512; i++){
            channels[i] = 0;
        }
    }



}
