package com.dmx_console.output;

public class SimulatedDMXOutput implements DMXOutput{

    @Override
    public void connect(){
        System.out.println("[SIM] Conectado al equipo DMX simulado");
    }

    @Override
    public void disconnect(){
        System.out.println("[SIM] Desconectado del equipo DMX simulado");
    }

    @Override
    public void sendUniverse(byte[] dmxData){

        System.out.println("[SIM] Enviando universo DMX: ");
        for(int i=0; i< dmxData.length; i++){
            int value = dmxData[i] & 0XFF; //convierte byte a int sin signo

            if(value>0){
                System.out.println("Canal " + (i+1) + ": " +
                        value);
            }
        }
    }
}
