package org.pilotpi.api;

/**
 * 
 * @author burlutal
 *
 */
public interface IMU {
	
	void init();
	void start();
	Gyroscope getGyroscope();
	Accelerometer getAccelerometer();
	Magnetometer getMagnetometer();
	
	float getPitch();
	float getRoll();
	float getYaw();
	
	void registerListener(IMUListener listener);
}
