package org.pilotpi.api;

import org.pilotpi.math.Vector;


//Uncomment the below line to use this axis definition: 
// X axis pointing forward
// Y axis pointing to the right 
// and Z axis pointing down.
//Positive pitch : nose up
//Positive roll : right wing down
//Positive yaw : clockwise
public class Main implements InertialMeasurementUnit {
	public static final Vector GYRO_SENSOR_SIGN = 	new Vector(1, 1, 1);
	public static final Vector ACCEL_SENSOR_SIGN = 	new Vector(-1, -1, -1);
	public static final Vector MAG_SENSOR_SIGN = 	new Vector(1, 1, 1);
	
	// LSM303 accelerometer: 8 g sensitivity
	// 3.9 mg/digit; 1 g = 256
	public static final int GRAVITY = 256;

	public static final float Gyro_Gain_X = 0.07f; //X axis Gyro gain
	public static final float Gyro_Gain_Y = 0.07f; //Y axis Gyro gain
	public static final float Gyro_Gain_Z = 0.07f; //Z axis Gyro gain
	
	public static final Vector Gyro_Gain = new Vector(0.07f);
	
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
	
	Vector gyroOffset = new Vector();
	Vector accelOffset = new Vector();
	
	Vector gyroData = new Vector();
	Vector accelData = new Vector();
	Vector magData = new Vector();

	Vector cMagnetom = new Vector();
	float MAG_Heading;

//	float[] Accel_Vector = {
//		0, 0, 0
//	}; // Store the acceleration in a vector
//	float[] Gyro_Vector = {
//		0, 0, 0
//	};// Store the gyros turn rate in a vector
//	float[] Omega_Vector = {
//		0, 0, 0
//	}; // Corrected Gyro_Vector data
//	float[] Omega_P = {
//		0, 0, 0
//	};// Omega Proportional correction
//	float[] Omega_I = {
//		0, 0, 0
//	};// Omega Integrator
//	float[] Omega = {
//		0, 0, 0
//	};

	Vector Accel_Vector = new Vector( 0, 0, 0);// Store the acceleration in a vector
	Vector Gyro_Vector = new Vector( 0, 0, 0);// Store the gyros turn rate in a vector
	Vector Omega_Vector = new Vector( 0, 0, 0);// Corrected Gyro_Vector data
	Vector Omega_P = new Vector( 0, 0, 0);// Omega Proportional correctio
	Vector Omega_I = new Vector( 0, 0, 0);// Omega Integrator
	Vector Omega = new Vector( 0, 0, 0);

	// Euler angles
	float roll;
	float pitch;
	float yaw;

	Vector errorRollPitch = new Vector( 0, 0, 0);
	Vector errorYaw = new Vector( 0, 0, 0);

	int counter = 0;
	byte gyro_sat = 0;

	Vector[] DCM_Matrix = {
		new Vector(1, 0, 0),
		new Vector(0, 1, 0),
		new Vector(0, 0, 1)
	};
	Vector[] Update_Matrix = {
		new Vector(0, 1, 2),
		new Vector(3, 4, 5),
		new Vector(6, 7, 8)
	}; // Gyros here

	Vector[] Temporary_Matrix = {
		new Vector(0, 0, 0),
		new Vector(0, 0, 0),
		new Vector(0, 0, 0)
	};

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
		Vector gyroData = new Vector();
		Vector accelData = new Vector();
		for (int i = 0; i < 32; i++) {
			gyroscope.update();
			accelerometer.updateAcc();
			
			gyroscope.readGyro(gyroData);
			accelerometer.readAcc(accelData);
			
			gyroOffset.add(gyroData);
			accelOffset.add(accelData);
			delay();			
		}
		gyroOffset.devide(32);
		accelOffset.devide(32);
		accelOffset.substract(new Vector(0, 0, GRAVITY * ACCEL_SENSOR_SIGN.getZ()));
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

	public void loop(){
		long timer = System.currentTimeMillis();
		for(int i = 1;;i++){
			if((System.currentTimeMillis() - timer) >= 20)  // Main loop runs at 50Hz
			{
			    timer_old = timer;
			    timer=System.currentTimeMillis();
			    if (timer > timer_old)
			      G_Dt = (timer-timer_old)/1000.0f;  // Real time of loop run. We use this on the DCM algorithm (gyro integration time)
			    else
			      G_Dt = 0;
			}
			gyroscope.update();
			accelerometer.updateAcc();
			
			
			gyroscope.readGyro(gyroData);
			accelerometer.readAcc(accelData);
			if(i%5 == 0){
				magnetometer.updateMag();
				magnetometer.readMag(magData);
				
				magData.multiply(MAG_SENSOR_SIGN);

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

	void Normalize()
	{
	  float error=0;
	  float[][] temporary = {{0,0,0},{0,0,0},{0,0,0}}; 
	  float renorm=0;
	  
	  error = vectorDotProduct(DCM_Matrix[0], DCM_Matrix[1]) * -.5f; //eq.19

	  Vector_Scale(temporary[0], DCM_Matrix[1], error); //eq.19
	  Vector_Scale(temporary[1], DCM_Matrix[0],error); //eq.19
	  
	  Vector_Add(temporary[0], temporary[0], DCM_Matrix[0]);//eq.19
	  Vector_Add(temporary[1], temporary[1], DCM_Matrix[1]);//eq.19
	  
	  Vector_Cross_Product(temporary[2], temporary[0], temporary[1]); // c= a x b //eq.20
	  
	  renorm= .5f *(3 - vectorDotProduct(temporary[0], temporary[0])); //eq.21
	  Vector_Scale(DCM_Matrix[0], temporary[0], renorm);
	  
	  renorm= .5f *(3 - vectorDotProduct(temporary[1], temporary[1])); //eq.21
	  Vector_Scale(DCM_Matrix[1], temporary[1], renorm);
	  	  
	  renorm= .5f *(3 - vectorDotProduct(temporary[2], temporary[2])); //eq.21
	  Vector_Scale(DCM_Matrix[2], temporary[2], renorm);

	}

	/**************************************************/
	void Drift_correction()
	{
	  float mag_heading_x;
	  float mag_heading_y;
	  float errorCourse;
	  //Compensation the Roll, Pitch and Yaw drift. 
	  float Scaled_Omega_P[] = {0,0,0};
	  float Scaled_Omega_I[] = {0,0,0};
	  float Accel_magnitude;
	  float Accel_weight;
	  
	  
	  //*****Roll and Pitch***************

	  // Calculate the magnitude of the accelerometer vector
	  Accel_magnitude = (float) Math.sqrt(Accel_Vector[0]*Accel_Vector[0] + Accel_Vector[1]*Accel_Vector[1] + Accel_Vector[2]*Accel_Vector[2]);
	  Accel_magnitude = Accel_magnitude / GRAVITY; // Scale to gravity.
	  // Dynamic weighting of accelerometer info (reliability filter)
	  // Weight for accelerometer info (<0.5G = 0.0, 1G = 1.0 , >1.5G = 0.0)
	  Accel_weight = constrain(1 - 2*Math.abs(1 - Accel_magnitude),0,1);  //  

	  Vector_Cross_Product(errorRollPitch,Accel_Vector,DCM_Matrix[2]); //adjust the ground of reference
	  Vector_Scale(Omega_P,errorRollPitch,Kp_ROLLPITCH*Accel_weight);
	  
	  Vector_Scale(Scaled_Omega_I,errorRollPitch,Ki_ROLLPITCH*Accel_weight);
	  Vector_Add(Omega_I,Omega_I,Scaled_Omega_I);     
	  
	  //*****YAW***************
	  // We make the gyro YAW drift correction based on compass magnetic heading
	 
	  mag_heading_x = (float) Math.cos(MAG_Heading);
	  mag_heading_y = (float) Math.sin(MAG_Heading);
	  errorCourse=(DCM_Matrix[0][0]*mag_heading_y) - (DCM_Matrix[1][0]*mag_heading_x);  //Calculating YAW error
	  Vector_Scale(errorYaw,DCM_Matrix[2],errorCourse); //Applys the yaw correction to the XYZ rotation of the aircraft, depeding the position.
	  
	  Vector_Scale(Scaled_Omega_P,errorYaw,Kp_YAW);//.01proportional of YAW.
	  Vector_Add(Omega_P,Omega_P,Scaled_Omega_P);//Adding  Proportional.
	  
	  Vector_Scale(Scaled_Omega_I,errorYaw,Ki_YAW);//.00001Integrator
	  Vector_Add(Omega_I,Omega_I,Scaled_Omega_I);//adding integrator to the Omega_I
	}

	/**
	 * Constrains a number to be within a range.
	 * 
	 * @param x
	 * @param a
	 * @param b
	 * @return 
	 * x: the number to constrain
	 * a: the lower end of the range
	 * b: the upper end of the range
	 */
	private float constrain(float x, int a, int b) {
		if(a <= x && x <= b){
			return x;
		} else if(x < a) {
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

	void Matrix_update()
	{
	  Gyro_Vector.setX((float) Gyro_Scaled(gyroData.getX(), Gyro_Gain_X)); //gyro x roll
	  Gyro_Vector.setY((float) Gyro_Scaled(gyroData.getY(), Gyro_Gain_Y)); //gyro y roll
	  Gyro_Vector.setZ((float) Gyro_Scaled(gyroData.getZ(), Gyro_Gain_Z)); //gyro z roll
	  
	  Accel_Vector.set(accelData);
	    
	  Omega_Vector.clear();
	  Omega_Vector.add(Gyro_Vector);//adding proportional term
	  Omega_Vector.add(Omega_I);//adding Integrator term
	  Omega_Vector.add(Omega_P);
	  
	  Update_Matrix[0].set(0, -G_Dt * Omega_Vector.getZ(), G_Dt*Omega_Vector.getY());
	  Update_Matrix[1].set(G_Dt*Omega_Vector.getZ(), 0, -G_Dt*Omega_Vector.getX());
	  Update_Matrix[2].set(-G_Dt*Omega_Vector.getY(), G_Dt*Omega_Vector.getX(), 0);

	  Matrix_Multiply(DCM_Matrix,Update_Matrix,Temporary_Matrix); //a*b=c

	  for(int x=0; x<3; x++) //Matrix Addition (update)
	  {
	    for(int y=0; y<3; y++)
	    {
	      DCM_Matrix[x][y]+=Temporary_Matrix[x][y];
	    } 
	  }
	}

	void Euler_angles() {
		pitch = (float) (-1 * Math.asin(DCM_Matrix[2].getX()));
		roll = (float) Math.atan2(DCM_Matrix[2].getY(), DCM_Matrix[2].getZ());
		yaw = (float) Math.atan2(DCM_Matrix[1].getX(), DCM_Matrix[0].getX());
	}

	float vectorDotProduct(Vector v1, Vector v2) {
		return v1.getX()*v2.getX() + v1.getY()*v2.getY() + v1.getY()*v2.getY();
	}
	
	// Computes the cross product of two vectors
	void Vector_Cross_Product(Vector vectorOut, Vector v1, Vector v2) {
		vectorOut.setX((v1.getY() * v2.getZ()) - (v1.getZ() * v2.getY()));
		vectorOut.setY((v1.getZ() * v2.getX()) - (v1.getX() * v2.getZ()));
		vectorOut.setZ((v1.getX() * v2.getY()) - (v1.getY() * v2.getZ()));
	}

	// Multiply the vector by a scalar.
	void Vector_Scale(float vectorOut[], float vectorIn[], float scale2) {
		for (int c = 0; c < 3; c++) {
			vectorOut[c] = vectorIn[c] * scale2;
		}
	}

	void Vector_Add(float vectorOut[], float vectorIn1[], float vectorIn2[]) {
		vectorOut.add
		for (int c = 0; c < 3; c++) {
			vectorOut[c] = vectorIn1[c] + vectorIn2[c];
		}
	}

	// Multiply two 3x3 matrixs. This function developed by Jordi can be easily
	// adapted to multiple n*n matrix's. (Pero me da flojera!).
	void Matrix_Multiply(Vector[] a, Vector[] b, Vector[] mat) {
		Vector op = new Vector();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int w = 0; w < 3; w++) {
					op[w] = a[x][w] * b[w][y];
				}
				mat[x][y] = 0;
				mat[x][y] = op[0] + op[1] + op[2];

				float test = mat[x][y];
			}
		}
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

		cMagnetom.setX((magData.getX() - MAG_SENSOR_SIGN.getX() * M_X_MIN) / (M_X_MAX - M_X_MIN) - MAG_SENSOR_SIGN.getX() * 0.5f);
		cMagnetom.setY((magData.getY() - MAG_SENSOR_SIGN.getY() * M_Y_MIN) / (M_Y_MAX - M_Y_MIN) - MAG_SENSOR_SIGN.getY() * 0.5f);
		cMagnetom.setZ((magData.getZ() - MAG_SENSOR_SIGN.getZ() * M_Z_MIN) / (M_Z_MAX - M_Z_MIN) - MAG_SENSOR_SIGN.getZ() * 0.5f);

		// Tilt compensated Magnetic filed X:
		MAG_X = cMagnetom.getX() * cos_pitch + cMagnetom.getY() * sin_roll * sin_pitch + cMagnetom.getZ()* cos_roll * sin_pitch;
		// Tilt compensated Magnetic filed Y:
		MAG_Y = cMagnetom.getY()* cos_roll - cMagnetom.getZ() * sin_roll;
		// Magnetic Heading
		MAG_Heading = (float) Math.atan2(-MAG_Y, MAG_X);
	}
	
	double Gyro_Scaled(float x, float gyro_gain){ 
		return (x)*toRad(gyro_gain); //Return the scaled ADC raw data of the gyro in radians for second
	}
	
}
