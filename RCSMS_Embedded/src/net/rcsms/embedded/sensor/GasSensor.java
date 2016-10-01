package net.rcsms.embedded.sensor;

import net.rcsms.embedded.ic.MCP3008;

public class GasSensor {

    private MCP3008 mcp3008;
    private MCP3008.MCP3008Channels channel;

    public GasSensor(MCP3008 mcp3008, MCP3008.MCP3008Channels channel) {
        this.mcp3008 = mcp3008;
        this.channel = channel;
    }

    public int detect() {
        double RATE = 100D / 1023;
        // 讀取 MCP3008 指定的 channel 值
        int adcValue = mcp3008.read(channel.getChannel());
        int value = (int) (adcValue * RATE);
        
        return value;
    }

}
