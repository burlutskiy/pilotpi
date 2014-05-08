package org.pilotpi.api;

import org.pilotpi.math.Vector;

public interface Magnetometer {
	void initMag();
	void readMag(Vector v);
	void updateMag();
}
