package org.pilotpi.sensors.l3g;

import java.io.IOException;

import javax.vecmath.Vector3f;

import org.pilotpi.api.Gyroscope;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class L3GyroSensor implements Gyroscope {
	I2CBus bus;
	I2CDevice device;
	Vector3f vector;
	
	public L3GyroSensor() {
		try {
			vector = new Vector3f();
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
			device.write(Constants.L3G_CTRL_REG1, (byte) 0x0F); // normal power
			device.write(Constants.L3G_CTRL_REG4, (byte) 0x20); // 2000 dps full scale
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override
	public void update() {
		try {
			int xlg = device.read(Constants.L3G_OUT_X_L);
			int xhg = device.read(Constants.L3G_OUT_X_H);
			int ylg = device.read(Constants.L3G_OUT_Y_L);
			int yhg = device.read(Constants.L3G_OUT_Y_H);
			int zlg = device.read(Constants.L3G_OUT_Z_L);
			int zhg = device.read(Constants.L3G_OUT_Z_H);
			vector.setX(xhg << 8 | xlg);
			vector.setY(yhg << 8 | ylg);
			vector.setZ(zhg << 8 | zlg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readGyro(Vector3f v) {
		v.set(vector);
	}
}
