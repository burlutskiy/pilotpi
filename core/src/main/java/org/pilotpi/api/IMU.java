package org.pilotpi.api;

/**
 * 
 * @author burlutal
 *
 */
public interface IMU {
	
	void init();
	void start();
	void stop();
	Gyroscope getGyroscope();
	Accelerometer getAccelerometer();
	Magnetometer getMagnetometer();
	
	double getPitch();
	double getRoll();
	double getYaw();
	
	void registerListener(IMUListener listener);
}
