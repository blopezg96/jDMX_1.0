package output;

import dmx.Universe;

public interface DMXOutput {

    void connect();
    void disconnect();

    void sendUniverse(byte[] dmxData); // siempre 512 bytes
}
