package org.pilotpi.api.impl;

import org.pilotpi.api.IMU;
import org.pilotpi.api.IMUListener;

public class IMUController {
	IMU imu;
	IMUListener imuListener;
	Thread imuThread;
	
	public IMUController() {
		imu = new IMUImpl();
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
