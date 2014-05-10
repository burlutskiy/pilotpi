package org.pilotpi.api;

import javax.vecmath.Vector3d;

public interface Gyroscope {
	void initGyro();
	void readGyro(Vector3d v);
	void update();
}
