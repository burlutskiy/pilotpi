package org.pilotpi.api;


//Uncomment the below line to use this axis definition: 
// X axis pointing forward
// Y axis pointing to the right 
// and Z axis pointing down.
//Positive pitch : nose up
//Positive roll : right wing down
//Positive yaw : clockwise
public class Main implements InertialMeasurementUnit {
	int SENSOR_SIGN[] = new int[] {
		1, 1, 1, -1, -1, -1, 1, 1, 1
	};
	// LSM303 accelerometer: 8 g sensitivity
	// 3.9 mg/digit; 1 g = 256
	public static final int GRAVITY = 256;

	public static final float Gyro_Gain_X = 0.07f; //X axis Gyro gain
	public static final float Gyro_Gain_Y = 0.07f; //Y axis Gyro gain
	public static final float Gyro_Gain_Z = 0.07f; //Z axis Gyro gain
	
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
	int[] AN = new int[9]; // array that stores the gyro and accelerometer data
	int[] AN_OFFSET = new int[] {
		0, 0, 0, 0, 0, 0
	}; // Array that stores the Offset of the sensors

	int gyro_x;
	int gyro_y;
	int gyro_z;
	int accel_x;
	int accel_y;
	int accel_z;
	int magnetom_x;
	int magnetom_y;
	int magnetom_z;
	float c_magnetom_x;
	float c_magnetom_y;
	float c_magnetom_z;
	float MAG_Heading;

	float[] Accel_Vector = {
		0, 0, 0
	}; // Store the acceleration in a vector
	float[] Gyro_Vector = {
		0, 0, 0
	};// Store the gyros turn rate in a vector
	float[] Omega_Vector = {
		0, 0, 0
	}; // Corrected Gyro_Vector data
	float[] Omega_P = {
		0, 0, 0
	};// Omega Proportional correction
	float[] Omega_I = {
		0, 0, 0
	};// Omega Integrator
	float[] Omega = {
		0, 0, 0
	};

	// Euler angles
	float roll;
	float pitch;
	float yaw;

	float[] errorRollPitch = new float[] {
		0, 0, 0
	};
	float[] errorYaw = new float[] {
		0, 0, 0
	};

	int counter = 0;
	byte gyro_sat = 0;

	float[][] DCM_Matrix = {
		{
			1, 0, 0
		}, {
			0, 1, 0
		}, {
			0, 0, 1
		}
	};
	float[][] Update_Matrix = {
		{
			0, 1, 2
		}, {
			3, 4, 5
		}, {
			6, 7, 8
		}
	}; // Gyros here

	float[][] Temporary_Matrix = {
		{
			0, 0, 0
		}, {
			0, 0, 0
		}, {
			0, 0, 0
		}
	};

	Gyroscope gyroscope;
	Magnetometer magnetometer;
	Accelerometer accelerometer;

	public void init() {
		gyroscope.init();
		magnetometer.init();
		accelerometer.init();
		initOffsets();
	}

	public void initOffsets() {
		for (int i = 0; i < 32; i++) {
			gyroscope.readXYZ(AN);
			accelerometer.readXYZ(AN, 3);
			for (int j = 0; j < 6; j++) {
				AN_OFFSET[j] += AN[j];
			}
			gyroscope.update();
			accelerometer.update();
			delay();
		}
		for (int i = 0; i < 6; i++) {
			AN_OFFSET[i] /= 32;
		}
		AN_OFFSET[5] -= GRAVITY * SENSOR_SIGN[5];
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
			accelerometer.update();
			
			gyroscope.readXYZ(AN);
			accelerometer.readXYZ(AN, 3);
			magnetometer.readXYZ(AN, 6);
			if(i%5 == 0){
				magnetom_x = SENSOR_SIGN[6] * AN[6];
				magnetom_y = SENSOR_SIGN[7] * AN[7];
				magnetom_z = SENSOR_SIGN[8] * AN[8];
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
	  Gyro_Vector[0]=(float) Gyro_Scaled(gyro_x, Gyro_Gain_X); //gyro x roll
	  Gyro_Vector[1]=(float) Gyro_Scaled(gyro_y, Gyro_Gain_Y); //gyro y pitch
	  Gyro_Vector[2]=(float) Gyro_Scaled(gyro_z, Gyro_Gain_Z); //gyro Z yaw
	  
	  Accel_Vector[0]=accel_x;
	  Accel_Vector[1]=accel_y;
	  Accel_Vector[2]=accel_z;
	    
	  Vector_Add(Omega, Gyro_Vector, Omega_I);  //adding proportional term
	  Vector_Add(Omega_Vector, Omega, Omega_P); //adding Integrator term

	  //Accel_adjust();    //Remove centrifugal acceleration.   We are not using this function in this version - we have no speed measurement
	  
	 if(OUTPUTMODE==1){         
	  Update_Matrix[0][0]=0;
	  Update_Matrix[0][1]=-G_Dt*Omega_Vector[2];//-z
	  Update_Matrix[0][2]=G_Dt*Omega_Vector[1];//y
	  Update_Matrix[1][0]=G_Dt*Omega_Vector[2];//z
	  Update_Matrix[1][1]=0;
	  Update_Matrix[1][2]=-G_Dt*Omega_Vector[0];//-x
	  Update_Matrix[2][0]=-G_Dt*Omega_Vector[1];//-y
	  Update_Matrix[2][1]=G_Dt*Omega_Vector[0];//x
	  Update_Matrix[2][2]=0;
	 }
	 else {
	  Update_Matrix[0][0]=0;
	  Update_Matrix[0][1]=-G_Dt*Gyro_Vector[2];//-z
	  Update_Matrix[0][2]=G_Dt*Gyro_Vector[1];//y
	  Update_Matrix[1][0]=G_Dt*Gyro_Vector[2];//z
	  Update_Matrix[1][1]=0;
	  Update_Matrix[1][2]=-G_Dt*Gyro_Vector[0];
	  Update_Matrix[2][0]=-G_Dt*Gyro_Vector[1];
	  Update_Matrix[2][1]=G_Dt*Gyro_Vector[0];
	  Update_Matrix[2][2]=0;
	 }

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
		pitch = (float) (-1 * Math.asin(DCM_Matrix[2][0]));
		roll = (float) Math.atan2(DCM_Matrix[2][1], DCM_Matrix[2][2]);
		yaw = (float) Math.atan2(DCM_Matrix[1][0], DCM_Matrix[0][0]);
	}

	float vectorDotProduct(float vector1[], float vector2[]) {
		float op = 0;
		for (int c = 0; c < 3; c++) {
			op += vector1[c] * vector2[c];
		}
		return op;
	}
	
	// Computes the cross product of two vectors
	void Vector_Cross_Product(float vectorOut[], float v1[], float v2[]) {
		vectorOut[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
		vectorOut[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
		vectorOut[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
	}

	// Multiply the vector by a scalar.
	void Vector_Scale(float vectorOut[], float vectorIn[], float scale2) {
		for (int c = 0; c < 3; c++) {
			vectorOut[c] = vectorIn[c] * scale2;
		}
	}

	void Vector_Add(float vectorOut[], float vectorIn1[], float vectorIn2[]) {
		for (int c = 0; c < 3; c++) {
			vectorOut[c] = vectorIn1[c] + vectorIn2[c];
		}
	}

	// Multiply two 3x3 matrixs. This function developed by Jordi can be easily
	// adapted to multiple n*n matrix's. (Pero me da flojera!).
	void Matrix_Multiply(float a[][], float b[][], float mat[][]) {
		float op[] = {
			0, 0, 0
		};
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
		c_magnetom_x = (float) ((float) (magnetom_x - SENSOR_SIGN[6] * M_X_MIN) / (M_X_MAX - M_X_MIN) - SENSOR_SIGN[6] * 0.5);
		c_magnetom_y = (float) ((float) (magnetom_y - SENSOR_SIGN[7] * M_Y_MIN) / (M_Y_MAX - M_Y_MIN) - SENSOR_SIGN[7] * 0.5);
		c_magnetom_z = (float) ((float) (magnetom_z - SENSOR_SIGN[8] * M_Z_MIN) / (M_Z_MAX - M_Z_MIN) - SENSOR_SIGN[8] * 0.5);

		// Tilt compensated Magnetic filed X:
		MAG_X = c_magnetom_x * cos_pitch + c_magnetom_y * sin_roll * sin_pitch + c_magnetom_z * cos_roll * sin_pitch;
		// Tilt compensated Magnetic filed Y:
		MAG_Y = c_magnetom_y * cos_roll - c_magnetom_z * sin_roll;
		// Magnetic Heading
		MAG_Heading = (float) Math.atan2(-MAG_Y, MAG_X);
	}
	
	double Gyro_Scaled(float x, float gyro_gain){ 
		return (x)*toRad(gyro_gain); //Return the scaled ADC raw data of the gyro in radians for second
	}
	
}
