package org.pilotpi.api.impl;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;

import org.pilotpi.api.Accelerometer;
import org.pilotpi.api.Gyroscope;
import org.pilotpi.api.IMU;
import org.pilotpi.api.IMUListener;
import org.pilotpi.api.Magnetometer;
import org.pilotpi.api.Vector;
import org.pilotpi.sensors.l3g.L3GyroSensor;
import org.pilotpi.sensors.lsm.LSM303Sensor;

//Uncomment the below line to use this axis definition: 
// X axis pointing forward
// Y axis pointing to the right 
// and Z axis pointing down.
//Positive pitch : nose up
//Positive roll : right wing down
//Positive yaw : clockwise
public class IMUImpl implements IMU {
	public static final Vector GYRO_SENSOR_SIGN = new Vector(1, 1, 1);
	public static final Vector ACCEL_SENSOR_SIGN = new Vector(-1, -1, -1);
	public static final Vector MAG_SENSOR_SIGN = new Vector(1, 1, 1);

	// LSM303 accelerometer: 8 g sensitivity
	// 3.9 mg/digit; 1 g = 256
	public static final int GRAVITY = 256;

	public static final double Gyro_Gain_X = 0.07f; // X axis Gyro gain
	public static final double Gyro_Gain_Y = 0.07f; // Y axis Gyro gain
	public static final double Gyro_Gain_Z = 0.07f; // Z axis Gyro gain

	public static final Vector Gyro_Gain = new Vector(0.07f, 0.07f, 0.07f);

	// LSM303 magnetometer calibration constants; use the Calibrate example from
	// the Pololu LSM303 library to find the right values for your board
	public static final int M_X_MIN = -152;
	public static final int M_Y_MIN = -252;
	public static final int M_Z_MIN = -204;
	public static final int M_X_MAX = 287;
	public static final int M_Y_MAX = 255;
	public static final int M_Z_MAX = 474;

	public static final double Kp_ROLLPITCH = 0.02f;
	public static final double Ki_ROLLPITCH = 0.00002f;
	public static final double Kp_YAW = 1.2f;
	public static final double Ki_YAW = 0.00002f;

	/* For debugging purposes */
	// OUTPUTMODE=1 will print the corrected data,
	// OUTPUTMODE=0 will print uncorrected data of the gyros (with drift)
	public static final double OUTPUTMODE = 1;

	// #define PRINT_DCM 0 //Will print the whole direction cosine matrix
	public static final double PRINT_ANALOGS = 0; // Will print the analog raw
													// data
	public static final double PRINT_EULER = 1; // Will print the Euler angles
												// Roll, Pitch and Yaw
	public static final double STATUS_LED = 13;

	double G_Dt = 0.2f; // Integration time (DCM algorithm) We will run the
						// integration loop at 50Hz if possible

	long timer = 0; // general purpuse timer
	long timer_old;
	long timer24 = 0; // Second timer used to print values

	Vector gyroOffset = new Vector();
	Vector accelOffset = new Vector();
	
	Vector gyroData = new Vector();
	Vector accelData = new Vector();
	Vector magData = new Vector();

	Vector cMagnetom = new Vector();
	double magHeading;

	Vector accelVector = new Vector(0, 0, 0);// Store the acceleration in a Vector
	Vector gyroVector = new Vector(0, 0, 0);// Store the gyros turn rate in a vector
	Vector omegaVector = new Vector(0, 0, 0);// Corrected Gyro_Vector data
	Vector omegaP = new Vector(0, 0, 0);// Omega Proportional correctio
	Vector omegaI = new Vector(0, 0, 0);// Omega Integrator
	Vector omega = new Vector(0, 0, 0);

	// Euler angles
	double roll;
	double pitch;
	double yaw;

	Vector errorRollPitch = new Vector(0, 0, 0);
	Vector errorYaw = new Vector(0, 0, 0);

	int counter = 0;
	byte gyro_sat = 0;

	Matrix3d dcmMatrix = new Matrix3d(1, 0, 0, 0, 1, 0, 0, 0, 1);
	Matrix3d updateMatrix = new Matrix3d(0, 1, 2, 3, 4, 5, 6, 7, 8);// Gyros
																		// here

	Matrix3d temporaryMatrix = new Matrix3d();
	Vector row1 = new Vector();
	Vector row2 = new Vector();
	Vector tmpVectorX = new Vector();
	Vector tmpVectorY = new Vector();
	Vector tmpVectorZ = new Vector();

	Gyroscope gyroscope;
	Magnetometer magnetometer;
	Accelerometer accelerometer;

	IMUListener listener;
	private boolean isRunning;
	
	public void init() {
		gyroscope.initGyro();
		magnetometer.initMag();
		accelerometer.initAcc();
		initOffsets();
		isRunning = true;
	}

	public void initOffsets() {
		Vector offsetG = new Vector();
		Vector offsetA = new Vector();
		for (int i = 0; i < 32; i++) {
			readGyro();
			readAccel();
			
			offsetG.add(gyroData);
			offsetA.add(accelData);
			delay();
		}
		gyroOffset.scale(1 / 32f, offsetG);
		accelOffset.scale(1 / 32f, offsetA);
		accelOffset.sub(new Vector(0, 0, GRAVITY * ACCEL_SENSOR_SIGN.z));
	}

	private double toRad(double x) {
		return  (x * 0.01745329252d); // *pi/180
	}



	public Gyroscope getGyroscope() {
		return gyroscope;
	}

	public void setGyroscope(Gyroscope gyroscope) {
		this.gyroscope = gyroscope;
	}

	public Magnetometer getMagnetometer() {
		return magnetometer;
	}

	public void setMagnetometer(Magnetometer magnetometer) {
		this.magnetometer = magnetometer;
	}

	public Accelerometer getAccelerometer() {
		return accelerometer;
	}

	public void setAccelerometer(Accelerometer accelerometer) {
		this.accelerometer = accelerometer;
	}

	public double getRoll() {
		return roll;
	}

	public double getPitch() {
		return pitch;
	}

	public double getYaw() {
		return yaw;
	}

	private static void delay() {
		try {
			Thread.sleep(20);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		long timer = System.currentTimeMillis();
		for (int i = 0;isRunning;i++) {
//			if ((System.currentTimeMillis() - timer) >= 20) // Main loop runs at 50Hz
//			{
				timer_old = timer;
				timer = System.currentTimeMillis();
				if (timer > timer_old)
					G_Dt = (timer - timer_old) / 1000.0f; // Real time of loop run. We use this on the DCM algorithm (gyro integration time)
				else
					G_Dt = 0;
//			}
			readGyro();
			readAccel();
			if (i >= 5) {
				i = 0;
				readMag();
				Compass_Heading();
			}

			// Calculations...
			Matrix_update();
			Normalize();
			Drift_correction();
			Euler_angles();
			// ***
			if(listener != null){
				listener.update(roll, pitch, yaw);
			}
		}
	}

	private void readMag() {
		magnetometer.updateMag();
		magnetometer.readMag(magData);

		magData.multiply(MAG_SENSOR_SIGN);
	}

	private void readAccel() {
		accelerometer.updateAcc();
		accelerometer.readAcc(accelData);
		accelData.sub(accelOffset);
		accelData.multiply(ACCEL_SENSOR_SIGN);
	}

	private void readGyro() {
		gyroscope.update();
		gyroscope.readGyro(gyroData);
		gyroData.sub(gyroOffset);
		gyroData.multiply(GYRO_SENSOR_SIGN);
	}

	public void stop() {
		isRunning = false;
	}
	
	private 
	
	void Normalize() {
		double error = 0;

		dcmMatrix.getRow(0, row1);
		dcmMatrix.getRow(1, row2);
		error = row1.dot(row2) * -.5f; // eq.19

		tmpVectorX.scale(error, row2);
		tmpVectorY.scale(error, row1);
		
		tmpVectorX.add(row1);	
		tmpVectorY.add(row2);	
		
		tmpVectorZ.cross(tmpVectorX, tmpVectorY);

		renormDMCMatrix(tmpVectorX, 0);
		renormDMCMatrix(tmpVectorY, 1);
		renormDMCMatrix(tmpVectorZ, 2);

	}

	private void renormDMCMatrix(Vector v, int index) {
		v.scale(.5f * (3 - v.lengthSquared()));
		dcmMatrix.setRow(index, v);
	}

	/**************************************************/
	void Drift_correction() {
		double magHeadingX;
		double magHeadingY;
		double errorCourse;
		// Compensation the Roll, Pitch and Yaw drift.
		Vector scaledOmegaP = new Vector();
		Vector scaledOmegaI = new Vector();
		double accelMagnitude;
		double accelWeight;

		// *****Roll and Pitch***************

		// Calculate the magnitude of the accelerometer vector
		accelMagnitude = accelVector.length();

		accelMagnitude = accelMagnitude / GRAVITY; // Scale to gravity.
		// Dynamic weighting of accelerometer info (reliability filter)
		// Weight for accelerometer info (<0.5G = 0.0, 1G = 1.0 , >1.5G = 0.0)
		accelWeight = constrain(1 - 2 * Math.abs(1 - accelMagnitude), 0, 1); //

		dcmMatrix.getRow(2, tmpVectorZ);
		
		errorRollPitch.cross(accelVector, tmpVectorZ); // adjust the ground of
		omegaP.scale(Kp_ROLLPITCH * accelWeight, errorRollPitch);

		scaledOmegaI.scale(Ki_ROLLPITCH * accelWeight, errorRollPitch);
		omegaI.add(scaledOmegaI);

		// *****YAW***************
		// We make the gyro YAW drift correction based on compass magnetic
		// heading

		magHeadingX =  Math.cos(magHeading);
		magHeadingY =  Math.sin(magHeading);
		errorCourse = (dcmMatrix.m00 * magHeadingY) - (dcmMatrix.m10 * magHeadingX); // Calculating YAW error
		errorYaw.scale(errorCourse, tmpVectorZ); // Applys the yaw correction to the XYZ rotation of the
													// aircraft, depeding the
													// position.

		scaledOmegaP.scale(Kp_YAW, errorYaw);
		omegaP.add(scaledOmegaP);// Adding Proportional.

		scaledOmegaI.scale(Ki_YAW, errorYaw);// .00001Integrator
		omegaI.add(scaledOmegaI);// adding integrator to the Omega_I
	}

	/**
	 * Constrains a number to be within a range.
	 * 
	 * @param x
	 * @param a
	 * @param b
	 * @return x: the number to constrain a: the lower end of the range b: the
	 *         upper end of the range
	 */
	private double constrain(double x, int a, int b) {
		if (a <= x && x <= b) {
			return x;
		} else if (x < a) {
			return a;
		} else {
			return b;
		}
	}

	void Matrix_update() {
		gyroVector.x = gyroData.x * toRad(Gyro_Gain_X); // gyro
		gyroVector.y = gyroData.y * toRad(Gyro_Gain_Y); // gyro
		gyroVector.z = gyroData.z * toRad(Gyro_Gain_Z); // gyro

		accelVector.set(accelData);

		omega.add(gyroVector, omegaI);
		omegaVector.add(omega ,omegaP);

		updateMatrix.setRow(0, 0, -G_Dt * omegaVector.z, G_Dt * omegaVector.y);
		updateMatrix.setRow(1, G_Dt * omegaVector.z, 0, -G_Dt * omegaVector.x);
		updateMatrix.setRow(2, -G_Dt * omegaVector.y, G_Dt * omegaVector.x, 0);

		temporaryMatrix.mul(dcmMatrix, updateMatrix);// a*b=c
		dcmMatrix.add(temporaryMatrix);
	}

	void Euler_angles() {
		pitch =  (-1 * Math.asin(dcmMatrix.m20));
		roll =  Math.atan2(dcmMatrix.m21, dcmMatrix.m22);
		yaw =  Math.atan2(dcmMatrix.m10, dcmMatrix.m00);
	}

	void Compass_Heading() {
		double MAG_X;
		double MAG_Y;
		double cos_roll;
		double sin_roll;
		double cos_pitch;
		double sin_pitch;

		cos_roll =  Math.cos(roll);
		sin_roll =  Math.sin(roll);
		cos_pitch =  Math.cos(pitch);
		sin_pitch =  Math.sin(pitch);

		// adjust for LSM303 compass axis offsets/sensitivity differences by
		// scaling to +/-0.5 range

		cMagnetom.setX((magData.getX() - MAG_SENSOR_SIGN.x * M_X_MIN) / (M_X_MAX - M_X_MIN) - MAG_SENSOR_SIGN.x * 0.5f);
		cMagnetom.setY((magData.getY() - MAG_SENSOR_SIGN.y * M_Y_MIN) / (M_Y_MAX - M_Y_MIN) - MAG_SENSOR_SIGN.y * 0.5f);
		cMagnetom.setZ((magData.getZ() - MAG_SENSOR_SIGN.z * M_Z_MIN) / (M_Z_MAX - M_Z_MIN) - MAG_SENSOR_SIGN.z * 0.5f);

		// Tilt compensated Magnetic filed X:
		MAG_X = cMagnetom.x * cos_pitch + cMagnetom.y * sin_roll * sin_pitch + cMagnetom.z * cos_roll * sin_pitch;
		// Tilt compensated Magnetic filed Y:
		MAG_Y = cMagnetom.y * cos_roll - cMagnetom.z * sin_roll;
		// Magnetic Heading
		magHeading =  Math.atan2(-MAG_Y, MAG_X);
	}

	double Gyro_Scaled(double x, double gyro_gain) {
		return x * toRad(gyro_gain); // Return the scaled ADC raw data of the gyro in radians for second
	}
	@Override
	public void registerListener(IMUListener listener) {
		this.listener = listener;
	}
	
}
