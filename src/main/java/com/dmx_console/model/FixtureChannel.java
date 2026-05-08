package com.dmx_console.model;

/* Esta clase representa un canal fisico */

public class FixtureChannel {

    private int offset;
    private ChannelFunction function;

    public FixtureChannel(int offset, ChannelFunction function){
        this.offset = offset;
        this.function = function;
    }

    public int getOffset(){
        return offset;
    }

    public ChannelFunction getFunction(){
        return function;
    }

}
