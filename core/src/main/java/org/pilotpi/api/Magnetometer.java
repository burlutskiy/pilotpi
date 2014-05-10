package org.pilotpi.api;

import javax.vecmath.Vector3d;

public interface Magnetometer {
	void initMag();
	void readMag(Vector3d v);
	void readMag(short[] v);
	void updateMag();
}
