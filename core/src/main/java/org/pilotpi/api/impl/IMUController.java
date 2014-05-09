package org.pilotpi.api.impl;

import org.pilotpi.api.IMU;
import org.pilotpi.api.IMUListener;
import org.pilotpi.sensors.l3g.L3GyroSensor;
import org.pilotpi.sensors.lsm.LSM303Sensor;

public class IMUController {
	IMU imu;
	IMUListener imuListener;
	Thread imuThread;
	
	public IMUController() {
		imu = new IMUImpl();
		LSM303Sensor lsm303Sensor = new LSM303Sensor();
		((IMUImpl)imu).setAccelerometer(lsm303Sensor);
		((IMUImpl)imu).setMagnetometer(lsm303Sensor);
		((IMUImpl)imu).setGyroscope(new L3GyroSensor());
		
		imuListener = new IMUListener() {
			@Override
			public void update(float roll, float pitch, float yaw) {
				System.out.println(String.format("roll: %.2f, pitch: %.2f, yaw: %.2f", roll, pitch, yaw));
			}
		};
	}
	public void init(){
		imu.init();
		imu.registerListener(imuListener);
		imuThread = new Thread(new Runnable() {
			@Override
			public void run() {
				imu.start();
			}
		});
	}
	public void startIMU(){
		imuThread.start();
	}

	public void stopIMU(){
		imuThread.interrupt();
	}
}
