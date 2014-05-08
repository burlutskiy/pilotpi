package org.pilotpi.sensors.lsm;

import java.io.IOException;

import org.pilotpi.math.Vector;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LSM303Sensor {
	I2CBus bus;
	I2CDevice device;

	static enum DeviceType {
		device_DLH, device_DLM, device_DLHC, device_D, device_auto
	}

	static enum Sa0State {
		sa0_low, sa0_high, sa0_auto
	};

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

	public void init() throws IOException {
		/*
		 * These values lead to an assumed magnetometer bias of 0. Use the
		 * Calibrate example program to determine appropriate values for your
		 * particular unit. The Heading example demonstrates how to adjust these
		 * values in your own sketch.
		 */
		m_min = new Vector(-32767, -32767, -32767);
		m_max = new Vector(-32767, -32767, -32767);

		deviceType = DeviceType.device_auto;

		io_timeout = 0; // 0 = no timeout
		did_timeout = false;

		bus = I2CFactory.getInstance(1);
		device = bus.getDevice(Constants.D_SA0_HIGH_ADDRESS);

		// Accelerometer
		// 0x57 = 0b01010111
		// AFS = 0 (+/- 2 g full scale)
		//device.write(Constants.CTRL2, (byte) 0x00);
		device.write(Constants.CTRL2, (byte) 0x18); // 8 g full scale: AFS = 011
		

		// 0x57 = 0b01010111
		// AODR = 0101 (50 Hz ODR); AZEN = AYEN = AXEN = 1 (all axes enabled)
		device.write(Constants.CTRL1, (byte) 0x57);

		// Magnetometer

		// 0x64 = 0b01100100
		// M_RES = 11 (high resolution mode); M_ODR = 001 (6.25 Hz ODR)
		device.write(Constants.CTRL5, (byte) 0x64);

		// 0x20 = 0b00100000
		// MFS = 01 (+/- 4 gauss full scale)
		device.write(Constants.CTRL6, (byte) 0x20);

		// 0x00 = 0b00000000
		// MLP = 0 (low power mode off); MD = 00 (continuous-conversion mode)
		device.write(Constants.CTRL7, (byte) 0x00);

	}

	// Reads the 3 accelerometer channels and stores them in vector a
	void readAcc() throws IOException {
		for (; (device.read(Constants.STATUS_A) & 0b00001000) >> 3 != 1; Thread.yield());
		int xla = device.read(Constants.OUT_X_L_A);
		int xha = device.read(Constants.OUT_X_H_A);
		int yla = device.read(Constants.OUT_Y_L_A);
		int yha = device.read(Constants.OUT_Y_H_A);
		int zla = device.read(Constants.OUT_Z_L_A);
		int zha = device.read(Constants.OUT_Z_H_A);

		// combine high and low bytes
		// This no longer drops the lowest 4 bits of the readings from the
		// DLH/DLM/DLHC, which are always 0
		// (12-bit resolution, left-aligned). The D has 16-bit resolution
		a.setX(xha << 8 | xla);
		a.setY(yha << 8 | yla);
		a.setZ(zha << 8 | zla);
	}

	// Reads the 3 accelerometer channels and stores them in vector a
	void readMag() throws IOException {
		int xlm, xhm, ylm, yhm, zlm, zhm;

		for (; (device.read(Constants.STATUS_M) & 0b00001000) >> 3 != 1; Thread.yield());
		// / D: X_L, X_H, Y_L, Y_H, Z_L, Z_H
		xlm = device.read(Constants.D_OUT_X_L_M);
		xhm = device.read(Constants.D_OUT_X_H_M);
		ylm = device.read(Constants.D_OUT_Y_L_M);
		yhm = device.read(Constants.D_OUT_Y_H_M);
		zlm = device.read(Constants.D_OUT_Z_L_M);
		zhm = device.read(Constants.D_OUT_Z_H_M);

		// combine high and low bytes
		m.setX(xhm << 8 | xlm);
		m.setX(yhm << 8 | ylm);
		m.setX(zhm << 8 | zlm);
	}

	public void update() {

	}

}
