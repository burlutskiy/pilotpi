// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   I2CExample.java

package org.pilotpi.examples;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class I2CExample
{

    public I2CExample()
    {
    }

    public static void main(String args[])
        throws IOException
    {
        I2CBus bus = I2CFactory.getInstance(1);
        System.out.println("Connected to bus OK!!!");
        int address = 107;
        I2CDevice device = bus.getDevice(address);
        System.out.println("Connected to device OK!!!");
        do
            System.out.print(device.read());
        while(true);
    }
}
