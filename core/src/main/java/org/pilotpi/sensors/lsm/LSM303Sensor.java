package org.pilotpi.sensors.lsm;

import java.io.IOException;

import org.pilotpi.math.Vector;


import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LSM303Sensor {
    I2CBus bus;
    I2CDevice device;	
    
	static enum DeviceType{ 
		device_DLH, device_DLM, device_DLHC, device_D, device_auto
	}
	static enum  Sa0State { sa0_low, sa0_high, sa0_auto };
	
	int address;
	DeviceType deviceType;
    byte acc_address;
    byte mag_address;

    static final int dummy_reg_count = 6;

    long io_timeout;
    boolean did_timeout;
    
	Vector a; // accelerometer readings
	Vector m; // magnetometer readings
	Vector m_max; // maximum magnetometer values, used for calibration
	Vector m_min; // minimum magnetometer values, used for calibration

	byte last_status; // status of last I2C transmission
	    
	public void init() throws IOException{
		  /*
		  These values lead to an assumed magnetometer bias of 0.
		  Use the Calibrate example program to determine appropriate values
		  for your particular unit. The Heading example demonstrates how to
		  adjust these values in your own sketch.
		  */
		  m_min = new Vector(-32767, -32767, -32767);
		  m_max = new Vector(32767, 32767, 32767);

		  deviceType = DeviceType.device_auto;

		  io_timeout = 0;  // 0 = no timeout
		  did_timeout = false;
		  
	      bus = I2CFactory.getInstance(1);
	      device = bus.getDevice(Constants.L3GD20_ADDRESS_SA0_HIGH);
	      device.write(Constants.L3G_CTRL_REG1, (byte)0x0F); // normal power mode, all axes enabled, 100 Hz
	      device.write(Constants.L3G_CTRL_REG4, (byte)0x20); // 2000 dps full scale

	}
	
	public void update(){
		
	}

}

