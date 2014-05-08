package org.pilotpi.api;

import org.pilotpi.math.Vector;

public interface Accelerometer {
	
	void initAcc();
	void readAcc(Vector v);
	void updateAcc();
}
