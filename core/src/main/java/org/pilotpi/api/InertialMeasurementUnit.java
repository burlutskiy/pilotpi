package org.pilotpi.api;

public interface InertialMeasurementUnit {
	
	void init();
	
	Gyroscope getGyroscope();
	Accelerometer getAccelerometer();
	Magnetometer getMagnetometer();
	
	float getPitch();
	float getRoll();
	float getYaw();
	
}
