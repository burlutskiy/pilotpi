package org.pilotpi.api;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

//Uncomment the below line to use this axis definition: 
// X axis pointing forward
// Y axis pointing to the right 
// and Z axis pointing down.
//Positive pitch : nose up
//Positive roll : right wing down
//Positive yaw : clockwise
public class Main implements InertialMeasurementUnit {
	public static final Vector3f GYRO_SENSOR_SIGN = new Vector3f(1, 1, 1);
	public static final Vector3f ACCEL_SENSOR_SIGN = new Vector3f(-1, -1, -1);
	public static final Vector3f MAG_SENSOR_SIGN = new Vector3f(1, 1, 1);

	// LSM303 accelerometer: 8 g sensitivity
	// 3.9 mg/digit; 1 g = 256
	public static final int GRAVITY = 256;

	public static final float Gyro_Gain_X = 0.07f; // X axis Gyro gain
	public static final float Gyro_Gain_Y = 0.07f; // Y axis Gyro gain
	public static final float Gyro_Gain_Z = 0.07f; // Z axis Gyro gain

	public static final Vector3f Gyro_Gain = new Vector3f(0.07f, 0.07f, 0.07f);

	// LSM303 magnetometer calibration constants; use the Calibrate example from
	// the Pololu LSM303 library to find the right values for your board
	public static final int M_X_MIN = -421;
	public static final int M_Y_MIN = -639;
	public static final int M_Z_MIN = -238;
	public static final int M_X_MAX = 424;
	public static final int M_Y_MAX = 295;
	public static final int M_Z_MAX = 472;

	public static final float Kp_ROLLPITCH = 0.02f;
	public static final float Ki_ROLLPITCH = 0.00002f;
	public static final float Kp_YAW = 1.2f;
	public static final float Ki_YAW = 0.00002f;

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

	float G_Dt = 0.02f; // Integration time (DCM algorithm) We will run the
						// integration loop at 50Hz if possible

	long timer = 0; // general purpuse timer
	long timer_old;
	long timer24 = 0; // Second timer used to print values

	Vector3f gyroOffset = new Vector3f();
	Vector3f accelOffset = new Vector3f();

	Vector3f gyroData = new Vector3f();
	Vector3f accelData = new Vector3f();
	Vector3f magData = new Vector3f();

	Vector3f cMagnetom = new Vector3f();
	float magHeading;

	Vector3f accelVector = new Vector3f(0, 0, 0);// Store the acceleration in a Vector
	Vector3f gyroVector = new Vector3f(0, 0, 0);// Store the gyros turn rate in a vector
	Vector3f omegaVector = new Vector3f(0, 0, 0);// Corrected Gyro_Vector data
	Vector3f omegaP = new Vector3f(0, 0, 0);// Omega Proportional correctio
	Vector3f omegaI = new Vector3f(0, 0, 0);// Omega Integrator
	Vector3f omega = new Vector3f(0, 0, 0);

	// Euler angles
	float roll;
	float pitch;
	float yaw;

	Vector3f errorRollPitch = new Vector3f(0, 0, 0);
	Vector3f errorYaw = new Vector3f(0, 0, 0);

	int counter = 0;
	byte gyro_sat = 0;

	Matrix3f dcmMatrix = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
	Matrix3f updateMatrix = new Matrix3f(0, 1, 2, 3, 4, 5, 6, 7, 8);// Gyros
																		// here

	Matrix3f temporaryMatrix = new Matrix3f();

	Gyroscope gyroscope;
	Magnetometer magnetometer;
	Accelerometer accelerometer;

	public void init() {
		gyroscope.init();
		magnetometer.initMag();
		accelerometer.initAcc();
		initOffsets();
	}

	public void initOffsets() {
		Vector3f gyroData = new Vector3f();
		Vector3f accelData = new Vector3f();
		for (int i = 0; i < 32; i++) {
			gyroscope.update();
			accelerometer.updateAcc();

			gyroscope.readGyro(gyroData);
			accelerometer.readAcc(accelData);

			gyroOffset.add(gyroData);
			accelOffset.add(accelData);
			delay();
		}
		gyroOffset.scale(1 / 32);
		accelOffset.scale(1 / 32);
		accelOffset.sub(new Vector3f(0, 0, GRAVITY * ACCEL_SENSOR_SIGN.getZ()));
	}

	private double toRad(double x) {
		return x * 0.01745329252d; // *pi/180
	}

	private double toDeg(double x) {
		return x * 57.2957795131d; // *180/pi
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

	public float getRoll() {
		return roll;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
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

	public void loop() {
		long timer = System.currentTimeMillis();
		for (int i = 1;; i++) {
			if ((System.currentTimeMillis() - timer) >= 20) // Main loop runs at
															// 50Hz
			{
				timer_old = timer;
				timer = System.currentTimeMillis();
				if (timer > timer_old)
					G_Dt = (timer - timer_old) / 1000.0f; // Real time of loop
															// run. We use this
															// on the DCM
															// algorithm (gyro
															// integration time)
				else
					G_Dt = 0;
			}
			gyroscope.update();
			accelerometer.updateAcc();

			gyroscope.readGyro(gyroData);
			accelerometer.readAcc(accelData);
			if (i % 5 == 0) {
				magnetometer.updateMag();
				magnetometer.readMag(magData);

				magData.scale(1, MAG_SENSOR_SIGN);

				Compass_Heading();
			}
			delay();

			// Calculations...
			Matrix_update();
			Normalize();
			Drift_correction();
			Euler_angles();
			// ***
		}
	}

	void Normalize() {
		float error = 0;

		Vector3f row1 = new Vector3f();
		Vector3f row2 = new Vector3f();
		Vector3f tmpVector1 = new Vector3f();
		Vector3f tmpVector2 = new Vector3f();
		Vector3f tmpVector3 = new Vector3f();

		dcmMatrix.getRow(0, row1);
		dcmMatrix.getRow(1, row2);
		error = row1.dot(row2) * -.5f; // eq.19

		tmpVector1.set(row2);
		tmpVector1.scaleAdd(error, tmpVector1);

		tmpVector2.set(row1);
		tmpVector2.scaleAdd(error, tmpVector2);

		tmpVector3.cross(tmpVector1, tmpVector2);

		renormDMCMatrix(tmpVector1, 0);
		renormDMCMatrix(tmpVector2, 1);
		renormDMCMatrix(tmpVector3, 2);

	}

	private void renormDMCMatrix(Vector3f v, int index) {
		v.scale(.5f * (3 - v.lengthSquared()));
		dcmMatrix.setRow(0, v);
	}

	/**************************************************/
	void Drift_correction() {
		float magHeadingX;
		float magHeadingY;
		float errorCourse;
		// Compensation the Roll, Pitch and Yaw drift.
		Vector3f scaledOmegaP = new Vector3f();
		Vector3f scaledOmegaI = new Vector3f();
		float accelMagnitude;
		float accelWeight;
		Vector3f tmpVectorZ = new Vector3f();

		// *****Roll and Pitch***************

		// Calculate the magnitude of the accelerometer vector
		accelMagnitude = accelVector.length();

		accelMagnitude = accelMagnitude / GRAVITY; // Scale to gravity.
		// Dynamic weighting of accelerometer info (reliability filter)
		// Weight for accelerometer info (<0.5G = 0.0, 1G = 1.0 , >1.5G = 0.0)
		accelWeight = constrain(1 - 2 * Math.abs(1 - accelMagnitude), 0, 1); //

		dcmMatrix.getRow(2, tmpVectorZ);
		errorRollPitch.cross(accelVector, tmpVectorZ); // adjust the ground of
														// reference

		omegaP.scale(Kp_ROLLPITCH * accelWeight, errorRollPitch);
		scaledOmegaI.scale(Ki_ROLLPITCH * accelWeight, errorRollPitch);
		omegaI.add(scaledOmegaI);

		// *****YAW***************
		// We make the gyro YAW drift correction based on compass magnetic
		// heading

		magHeadingX = (float) Math.cos(magHeading);
		magHeadingY = (float) Math.sin(magHeading);
		errorCourse = (dcmMatrix.m00 * magHeadingY) - (dcmMatrix.m10 * magHeadingX); // Calculating
																							// YAW
																							// error
		errorYaw.scale(errorCourse, tmpVectorZ); // Applys the yaw correction to
													// the XYZ rotation of the
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
	private float constrain(float x, int a, int b) {
		if (a <= x && x <= b) {
			return x;
		} else if (x < a) {
			return a;
		} else {
			return b;
		}
	}

	/**************************************************/
	/*
	 * void Accel_adjust(void) { Accel_Vector[1] +=
	 * Accel_Scale(speed_3d*Omega[2]); // Centrifugal force on Acc_y =
	 * GPS_speed*GyroZ Accel_Vector[2] -= Accel_Scale(speed_3d*Omega[1]); //
	 * Centrifugal force on Acc_z = GPS_speed*GyroY }
	 */
	/**************************************************/

	void Matrix_update() {
		gyroVector.setX((float) Gyro_Scaled(gyroData.x, Gyro_Gain_X)); // gyro
																				// x
																				// roll
		gyroVector.setY((float) Gyro_Scaled(gyroData.y, Gyro_Gain_Y)); // gyro
																				// y
																				// roll
		gyroVector.setZ((float) Gyro_Scaled(gyroData.z, Gyro_Gain_Z)); // gyro
																				// z
																				// roll

		accelVector.set(accelData);

		omegaVector.set(0, 0, 0);
		omegaVector.add(gyroVector);// adding proportional term
		omegaVector.add(omegaI);// adding Integrator term
		omegaVector.add(omegaP);

		updateMatrix.setRow(0, 0, -G_Dt * omegaVector.getZ(), G_Dt * omegaVector.getY());
		updateMatrix.setRow(1, G_Dt * omegaVector.getZ(), 0, -G_Dt * omegaVector.getX());
		updateMatrix.setRow(2, -G_Dt * omegaVector.getY(), G_Dt * omegaVector.getX(), 0);

		temporaryMatrix.mul(dcmMatrix, updateMatrix);// a*b=c
		dcmMatrix.add(temporaryMatrix);
	}

	void Euler_angles() {
		pitch = (float) (-1 * Math.asin(dcmMatrix.m20));
		roll = (float) Math.atan2(dcmMatrix.m21, dcmMatrix.m22);
		yaw = (float) Math.atan2(dcmMatrix.m10, dcmMatrix.m00);
	}

	void Compass_Heading() {
		float MAG_X;
		float MAG_Y;
		float cos_roll;
		float sin_roll;
		float cos_pitch;
		float sin_pitch;

		cos_roll = (float) Math.cos(roll);
		sin_roll = (float) Math.sin(roll);
		cos_pitch = (float) Math.cos(pitch);
		sin_pitch = (float) Math.sin(pitch);

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
		magHeading = (float) Math.atan2(-MAG_Y, MAG_X);
	}

	double Gyro_Scaled(float x, float gyro_gain) {
		return (x) * toRad(gyro_gain); // Return the scaled ADC raw data of the
										// gyro in radians for second
	}
}
