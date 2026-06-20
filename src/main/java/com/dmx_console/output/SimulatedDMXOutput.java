package com.dmx_console.output;
/* Muestra en consola el comportamiento simulado de el software con el hardware
* y el envio de informacion que se esta llevando a cabo  */
import java.lang.reflect.Array;
import java.util.Arrays;

public class SimulatedDMXOutput implements DMXOutput {

    private byte[] lastSent = new byte[512];
    private boolean logChanges = true;

    @Override
    public void connect() {
        System.out.println("[SIM] Conectado al equipo DMX simulado");
    }

    @Override
    public void disconnect() {
        System.out.println("[SIM] Desconectado del equipo DMX simulado");
    }

    @Override
    public void sendUniverse(byte[] dmxData) {
        if (!logChanges) return;

        if (!Arrays.equals(dmxData, lastSent)) {
            System.out.println("[SIM] Cambio detectado en universo DMX: ");
            for (int i = 0; i < dmxData.length; i++) {
                int value = dmxData[i] & 0XFF; //convierte byte a int sin signo
                int last = lastSent[i] & 0xFF;
                if (value != last) {
                    System.out.printf(" Canal %3d: %3d -> %3d%n",
                            i + 1, last, value);
                }
            }
            lastSent = Arrays.copyOf(dmxData, 512);
        }
    }

    public void setLogChanges(boolean log) {
        this.logChanges = log;
    }
}
