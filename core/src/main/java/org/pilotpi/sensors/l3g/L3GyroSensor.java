package org.pilotpi.sensors.l3g;

import java.io.IOException;

import javax.vecmath.Vector3d;

import org.pilotpi.api.Gyroscope;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class L3GyroSensor implements Gyroscope {
	I2CBus bus;
	I2CDevice device;
	private short x, y, z;
	
	public L3GyroSensor() {
		try {
			bus = I2CFactory.getInstance(1);
			device = bus.getDevice(Constants.L3GD20_ADDRESS_SA0_HIGH);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initGyro() {
		try {
//			device.write(Constants.L3G_LOW_ODR, (byte) 0x4f); // normal power
			device.write(Constants.L3G_CTRL_REG1, (byte) 0x0F); // normal power
//			device.write(Constants.L3G_CTRL_REG4, (byte) 0x20); // 2000 dps full scale
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override
	public void update() {
		try {
			
//			int status = device.read(Constants.L3G_STATUS_REG);
//			boolean xyzOverrun = 	(status & 0b1000_0000 >> 7) == 1;
//			boolean zOverrun = 		(status & 0b0100_0000 >> 7) == 1;
//			boolean yOverrun = 		(status & 0b0010_0000 >> 7) == 1;
//			boolean xOverrun = 		(status & 0b0001_0000 >> 7) == 1;
//			boolean xyzAvailable = 	(status & 0b0000_1000 >> 7) == 1;
//			boolean zAvailable = 	(status & 0b0000_0100 >> 7) == 1;
//			boolean yAvailable = 	(status & 0b0000_0010 >> 7) == 1;
//			boolean xAvailable = 	(status & 0b0000_0001 >> 7) == 1;

			for(; (device.read(Constants.L3G_STATUS_REG) & 0x8) >> 3 != 1; Thread.yield()){};
			 
			byte xlg = (byte) device.read(Constants.L3G_OUT_X_L);
			byte xhg = (byte) device.read(Constants.L3G_OUT_X_H);
			byte ylg = (byte) device.read(Constants.L3G_OUT_Y_L);
			byte yhg = (byte) device.read(Constants.L3G_OUT_Y_H);
			byte zlg = (byte) device.read(Constants.L3G_OUT_Z_L);
			byte zhg = (byte) device.read(Constants.L3G_OUT_Z_H);

			x =  (short) ((short) ((xhg & 0xFF) << 8 | xlg & 0xFF) >> 4);
			y =  (short) ((short) ((yhg & 0xFF) << 8 | ylg & 0xFF) >> 4);
			z =  (short) ((short) ((zhg & 0xFF) << 8 | zlg & 0xFF) >> 4);
		
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readGyro(Vector3d v) {
		v.set(x,y,z);
	}
	
	public static void main(String[] args) throws InterruptedException {
		L3GyroSensor gyroSensor = new L3GyroSensor();
		gyroSensor.initGyro();
		Vector3d gyro = new Vector3d();
		
		for(;;){
			gyroSensor.update();
			gyroSensor.readGyro(gyro);
			
			System.out.println(String.format("gyro:\t%.0f\t%.0f\t%.0f", gyro.x, gyro.y, gyro.z));
			
			Thread.sleep(250);
		}
	}

	@Override
	public void readGyro(short[] v) {
		v[0] = x;
		v[1] = y;
		v[2] = z;
	}

	public short getX() {
		return x;
	}

	public short getY() {
		return y;
	}

	public short getZ() {
		return z;
	}
	
	
}

