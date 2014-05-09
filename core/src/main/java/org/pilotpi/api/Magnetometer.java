package org.pilotpi.api;

import javax.vecmath.Vector3f;

public interface Magnetometer {
	void initMag();
	void readMag(Vector3f v);
	void updateMag();
}
