package org.pilotpi.api.impl;

import javax.vecmath.Vector3d;

import org.pilotpi.sensors.l3g.L3GyroSensor;
import org.pilotpi.sensors.lps.LPSensor;
import org.pilotpi.sensors.lsm.LSM303Sensor;

public class AltIMU10 {

	public static void main(String[] args) {
		LSM303Sensor lsm303Sensor = new LSM303Sensor();
		L3GyroSensor gyroSensor = new L3GyroSensor();
		LPSensor lpSensor = new LPSensor();
		
		lsm303Sensor.initAcc();
		lsm303Sensor.initMag();
		gyroSensor.initGyro();
		lpSensor.init();
		short[] gyro = {0,0,0};
		short[] acc = {0,0,0};
		short[] mag = {0,0,0};
		
		for (;;) {
			lsm303Sensor.updateAcc();
			lsm303Sensor.updateMag();
			gyroSensor.update();
			lpSensor.update();
			
			gyroSensor.readGyro(gyro);
			lsm303Sensor.readAcc(acc);
			lsm303Sensor.readMag(mag);
			
			String f = "g:\t%d\t%d\t%d\ta:\t%d\t%d\t%d\tm:\t%d\t%d\t%d\ta:\t%.02f";
			System.out.println(String.format(f, gyro[0], gyro[1], gyro[2], acc[0], acc[1], acc[2], mag[0], mag[1], mag[2], lpSensor.getAltitude()));
		}
	}
}
