package org.pilotpi.sensors.l3g;

import java.io.IOException;

import org.pilotpi.math.Vector;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class L3GyroSensor
{
    public L3GyroSensor()
    {
        vector = new Vector();
    }

    public void init()
        throws IOException
    {
        bus = I2CFactory.getInstance(1);
        device = bus.getDevice(Constants.L3GD20_ADDRESS_SA0_HIGH);
        device.write(Constants.L3G_CTRL_REG1, (byte)0x0F); // normal power mode, all axes enabled, 100 Hz
        device.write(Constants.L3G_CTRL_REG4, (byte)0x20); // 2000 dps full scale
    }

    public void update()
        throws IOException
    {
        byte xlg = (byte)device.read(Constants.L3G_OUT_X_L);
        byte xhg = (byte)device.read(Constants.L3G_OUT_X_H);
        byte ylg = (byte)device.read(Constants.L3G_OUT_Y_L);
        byte yhg = (byte)device.read(Constants.L3G_OUT_Y_H);
        byte zlg = (byte)device.read(Constants.L3G_OUT_Z_L);
        byte zhg = (byte)device.read(Constants.L3G_OUT_Z_H);
        vector.setX(xhg << 8 | xlg);
        vector.setY(yhg << 8 | ylg);
        vector.setZ(zhg << 8 | zlg);
    }

    public static void normalize(Vector v) {
        float f = (float)(Math.pow(v.getX(), 2D) + Math.pow(v.getY(), 2D) + Math.pow(v.getZ(), 2D));
        float mag = (float)Math.sqrt(f);
        System.out.println(String.format("%.4f\t%.4f\t%.4f", new Object[] {
            Float.valueOf((float)v.getX() / mag), Float.valueOf((float)v.getY() / mag), Float.valueOf((float)v.getZ() / mag)
        }));
    }

    public static void main(String args[]) throws IOException, InterruptedException
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
