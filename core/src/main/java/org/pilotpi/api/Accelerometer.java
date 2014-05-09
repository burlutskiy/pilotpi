package org.pilotpi.api;

import javax.vecmath.Vector3f;

public interface Accelerometer {
	
	void initAcc();
	void readAcc(Vector3f v);
	void updateAcc();
}
