package org.pilotpi.sensors.lsm;

import java.io.IOException;

import javax.vecmath.Vector3d;

import org.pilotpi.api.Accelerometer;
import org.pilotpi.api.Magnetometer;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LSM303Sensor implements Accelerometer, Magnetometer {
	I2CBus bus;
	I2CDevice device;
	volatile boolean initialized = false;

	int address;
	byte acc_address;
	byte mag_address;

	static final int dummy_reg_count = 6;

	long io_timeout;
	boolean did_timeout;

	short ax, ay, az, mx, my, mz;
	short m_max_x, m_max_y, m_max_z; // maximum magnetometer values, used for calibration
	short m_min_x, m_min_y, m_min_z; // minimum magnetometer values, used for calibration

	byte last_status; // status of last I2C transmission
	
	public LSM303Sensor() {
		try {
			/*
			 * These values lead to an assumed magnetometer bias of 0. Use the
			 * Calibrate example program to determine appropriate values for your
			 * particular unit. The Heading example demonstrates how to adjust these
			 * values in your own sketch.
			 */
			/*
			 * magne:	x(-3431	6081)	y(-3331	5503)	z(-1460	7515)
			 */
			m_max_x = m_max_y = m_max_z = -32767;
			m_min_x = m_min_y = m_min_z = 32767;

			io_timeout = 0; // 0 = no timeout
			did_timeout = false;

			bus = I2CFactory.getInstance(1);
			device = bus.getDevice(Constants.D_SA0_HIGH_ADDRESS);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Reads the 3 accelerometer channels and stores them in vector a
	void readAcc() throws IOException {
		for (; (device.read(Constants.STATUS_A) & 0x8) >> 3 != 1; Thread.yield());
		byte xla = (byte) device.read(Constants.OUT_X_L_A);
		byte xha = (byte) device.read(Constants.OUT_X_H_A);
		byte yla = (byte) device.read(Constants.OUT_Y_L_A);
		byte yha = (byte) device.read(Constants.OUT_Y_H_A);
		byte zla = (byte) device.read(Constants.OUT_Z_L_A);
		byte zha = (byte) device.read(Constants.OUT_Z_H_A);

		// combine high and low bytes
		// This no longer drops the lowest 4 bits of the readings from the
		// DLH/DLM/DLHC, which are always 0
		// (12-bit resolution, left-aligned). The D has 16-bit resolution
		ax = (short) ((xha & 0xff) << 8 | (xla & 0xff));
		ay = (short) ((yha & 0xff) << 8 | (yla & 0xff));
		az = (short) ((zha & 0xff) << 8 | (zla & 0xff));
		ax >>= 4;
		ay >>= 4;
		az >>= 4;
	}

	// Reads the 3 accelerometer channels and stores them in vector a
	void readMag() throws IOException {
		byte xlm, xhm, ylm, yhm, zlm, zhm;

		for (; (device.read(Constants.STATUS_M) & 0x8) >> 3 != 1; Thread.yield());
		// / D: X_L, X_H, Y_L, Y_H, Z_L, Z_H
		xlm = (byte) device.read(Constants.D_OUT_X_L_M);
		xhm = (byte) device.read(Constants.D_OUT_X_H_M);
		ylm = (byte) device.read(Constants.D_OUT_Y_L_M);
		yhm = (byte) device.read(Constants.D_OUT_Y_H_M);
		zlm = (byte) device.read(Constants.D_OUT_Z_L_M);
		zhm = (byte) device.read(Constants.D_OUT_Z_H_M);

		// combine high and low bytes
		mx = (short) ((xhm & 0xff) << 8 | (xlm& 0xff));
		my = (short) ((yhm & 0xff) << 8 | (ylm& 0xff));
		mz = (short) ((zhm & 0xff) << 8 | (zlm& 0xff));
		mx >>= 4;
		my >>= 4;
		mz >>= 4;
		
		m_min_x = (short) Math.min(m_min_x, mx);
		m_min_y = (short) Math.min(m_min_y, my);
		m_min_z = (short) Math.min(m_min_z, mz);
		m_max_x = (short) Math.max(m_max_x, mx);
		m_max_y = (short) Math.max(m_max_y, my);
		m_max_z = (short) Math.max(m_max_z, mz);
	}

	@Override
	public void initMag() {
		try {
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
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void readMag(Vector3d v) {
		v.set(mx,my,mz);
	}

	@Override
	public void updateMag() {
		try {
			readMag();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initAcc() {
		try{
			// Accelerometer
			// 0x57 = 0b01010111
			// AFS = 0 (+/- 2 g full scale)
			//device.write(Constants.CTRL2, (byte) 0x00);
			device.write(Constants.CTRL2, (byte) 0x18); // 8 g full scale: AFS = 011
			

			// 0x57 = 0b01010111
			// AODR = 0101 (50 Hz ODR); AZEN = AYEN = AXEN = 1 (all axes enabled)
			device.write(Constants.CTRL1, (byte) 0x57);			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void readAcc(Vector3d v) {
		v.set(ax, ay, az);
	}

	@Override
	public void updateAcc() {
		try {
			readAcc();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		LSM303Sensor lsm303Sensor = new LSM303Sensor();
		lsm303Sensor.initAcc();
		lsm303Sensor.initMag();
		short[] accel = {0,0,0};
		short[] mag = {0,0,0};
		
		for(;;){
			lsm303Sensor.updateAcc();
			lsm303Sensor.updateMag();

			lsm303Sensor.readAcc(accel);
			lsm303Sensor.readMag(mag);
			
			System.out.println(String.format("accel:\t%d\t%d\t%d\tmagne:\t%d(%d\t%d)\t%d(%d\t%d)\t%d(%d\t%d\theading:\t%.2f)", accel[0], accel[1], accel[2], mag[0], 
					lsm303Sensor.m_min_x, lsm303Sensor.m_max_x, mag[1], lsm303Sensor.m_min_y, lsm303Sensor.m_max_y, mag[2], lsm303Sensor.m_min_z, lsm303Sensor.m_max_z, lsm303Sensor.heading()));
			
			Thread.sleep(250);
		}
	}

	@Override
	public void readAcc(short[] v) {
		v[0] = ax;
		v[1] = ay;
		v[2] = az;
	}

	@Override
	public void readMag(short[] v) {
		v[0] = mx;
		v[1] = my;
		v[2] = mz;
	}
	double heading(){
	    // subtract offset (average of min and max) from magnetometer readings
		Vector3d temp_m = new Vector3d(mx,my,mz);
	    temp_m.x -= (m_min_x + m_max_x) / 2;
	    temp_m.y -= (m_min_y + m_max_y) / 2;
	    temp_m.z -= (m_min_z + m_max_z) / 2;

	    // compute E and N
	    Vector3d e = new Vector3d();
	    Vector3d n = new Vector3d();
	    Vector3d a = new Vector3d(az, ay, az);
		Vector3d from = new Vector3d(1, 0, 0);
	    e.cross(temp_m, a);
	    e.normalize();
	    n.cross(a, e);
	    n.normalize();

	    // compute heading
	    double heading = Math.atan2( e.dot(from), n.dot(from)) * 180 / Math.PI;
	    if (heading < 0) heading += 360;
	    return heading;
	}

}
