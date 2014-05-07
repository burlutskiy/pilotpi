// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   L3GyroSensor.java

package org.pilotpi.sensors.l3g;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class L3GyroSensor
{
    public static class Vector
    {

        int x;
        int y;
        int z;

        public Vector()
        {
            x = y = z = 0;
        }

        public Vector(float a, float b, float c)
        {
            x = (int)a;
            y = (int)b;
            z = (int)c;
        }
    }


    public L3GyroSensor()
    {
        vector = new Vector();
    }

    public void init()
        throws IOException
    {
        bus = I2CFactory.getInstance(1);
        device = bus.getDevice(107);
        device.write(32, (byte)15);
    }

    public void update()
        throws IOException
    {
        byte xlg = (byte)device.read(40);
        byte xhg = (byte)device.read(41);
        byte ylg = (byte)device.read(42);
        byte yhg = (byte)device.read(43);
        byte zlg = (byte)device.read(44);
        byte zhg = (byte)device.read(45);
        vector.x = xhg << 8 | xlg;
        vector.y = yhg << 8 | ylg;
        vector.z = zhg << 8 | zlg;
    }

    public static void normalize(Vector v)
    {
        float f = (float)(Math.pow(v.x, 2D) + Math.pow(v.z, 2D) + Math.pow(v.z, 2D));
        float mag = (float)Math.sqrt(f);
        System.out.println(String.format("%.4f\t%.4f\t%.4f", new Object[] {
            Float.valueOf((float)v.x / mag), Float.valueOf((float)v.y / mag), Float.valueOf((float)v.z / mag)
        }));
    }

    public static void main(String args[])
        throws IOException, InterruptedException
    {
        L3GyroSensor gyro = new L3GyroSensor();
        gyro.init();
        do
        {
            gyro.update();
            normalize(gyro.vector);
            Thread.sleep(200L);
        } while(true);
    }

    I2CBus bus;
    I2CDevice device;
    Vector vector;
}
