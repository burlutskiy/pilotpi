// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LPSensor.java

package org.pilotpi.sensors.lps;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LPSensor
{

    public LPSensor()
    {
    }

    public void init()
        throws IOException
    {
        bus = I2CFactory.getInstance(1);
        device = bus.getDevice(93);
        device.write(32, (byte)-32);
        device.write(16, (byte)122);
    }

    public void update()
        throws IOException
    {
        for(; (device.read(39) & 2) >> 1 != 1; Thread.yield());
        int pxl = device.read(40);
        int pl = device.read(41);
        int ph = device.read(42);
        pressure = ph << 16 | pl << 8 | pxl;
        int tl = device.read(43);
        int th = device.read(44);
        temperture = th << 8 | tl;
    }

    public double getAltitude()
    {
        double altimeter_setting_mbar = 1013.25D;
        return (1.0D - Math.pow((double)pressure / 4096D / altimeter_setting_mbar, 0.19026299999999999D)) * 44330.800000000003D;
    }

    public double getTemperture()
    {
        return (42.5D + (double)temperture / 480D) - 139D;
    }

    public static void main(String args[])
        throws IOException, InterruptedException
    {
        LPSensor sensor = new LPSensor();
        sensor.init();
        sensor.update();
        double zero_altitude = sensor.getAltitude();
        do
        {
            sensor.update();
            System.out.println(String.format("mbars: %.2f \t altitude: %.1f \t temperture: %.1f", new Object[] {
                Double.valueOf((double)sensor.pressure / 4096D), Double.valueOf(sensor.getAltitude() - zero_altitude), Double.valueOf(sensor.getTemperture())
            }));
            Thread.sleep(500L);
        } while(true);
    }

    I2CBus bus;
    I2CDevice device;
    long pressure;
    long temperture;
}
