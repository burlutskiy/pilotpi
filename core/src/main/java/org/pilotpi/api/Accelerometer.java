package org.pilotpi.api;

import javax.vecmath.Vector3d;

public interface Accelerometer {
	
	void initAcc();
	void readAcc(Vector3d v);
	void readAcc(short[] v);
	void updateAcc();
}
