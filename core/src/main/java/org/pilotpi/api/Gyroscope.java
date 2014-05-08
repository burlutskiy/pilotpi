package org.pilotpi.api;

import org.pilotpi.math.Vector;

public interface Gyroscope {
	void init();
	void readGyro(Vector v);
	void readXYZ(int[] xyz, int offset);
	int readX();
	int readY();
	int readZ();
	void update();
}
